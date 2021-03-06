package ie.cit.teambravo.cardsec.duration;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.Distance;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElementStatus;
import com.google.maps.model.Duration;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;

import ie.cit.teambravo.cardsec.location.LatLngAlt;

/**
 * Implementation of the DurationServiceAPI using Google API and Haversine formula
 */
@Service
public class MultiSourceDurationService implements DurationService {
	private static final int EARTH_RADIUS = 6371;
	private static final long MIN_DISTANCE_METRES = 100L;
	private static final long SECONDS_PER_FLOOR = 9L;
	private static final long METRES_PER_FLOOR = 3L;
	private static final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(3);
	private static final int ESTIMATED_PLANE_SPEED_METRES_PER_HR = 926000;
	private final GeoApiContext geoApiContext;

	@Autowired
	public MultiSourceDurationService(GeoApiContext geoApiContext) {
		this.geoApiContext = geoApiContext;
	}

	/**
	 * Concurrently computes the walking, driving and cycling distance between two points, if the distance of the
	 * minimum of these is less than 100 metres use the altitude and an estimate of floor time travel instead (which is
	 * estimated to be ~9 seconds, taking into account the current fastest 100 metre sprint of 9.58 seconds) to compute
	 * minimum time. Each result min distance between two points is cached.
	 * 
	 * @param start
	 *            the start position
	 * @param end
	 *            the end position
	 * @return the estimated minimum travel duration in seconds
	 */
	@Cacheable(value = "travelDuration", key = "{#start.getId(), #end.getId()}")
	public Long getTravelTimeBetween2Points(LatLngAlt start, LatLngAlt end) {
		LatLng startPoint = new LatLng(start.getLat(), start.getLng());
		LatLng endPoint = new LatLng(end.getLat(), end.getLng());

		List<CompletableFuture<Optional<Pair<Duration, Distance>>>> durationCalculations = Stream
				.of(TravelMode.DRIVING, TravelMode.BICYCLING, TravelMode.WALKING)
				.map(mode -> computeDurationAsync(startPoint, endPoint, mode)).collect(Collectors.toList());

		CompletableFuture<List<Optional<Pair<Duration, Distance>>>> computedPairs = CompletableFuture
				.allOf(durationCalculations.toArray(new CompletableFuture[durationCalculations.size()]))
				.thenApply(ignored -> durationCalculations.stream().map(CompletableFuture::join)
						.collect(Collectors.toList()));

		durationCalculations.forEach(calculation -> calculation.whenComplete((ignored, exception) -> {
			if (exception != null) {
				computedPairs.completeExceptionally(exception);
			}
		}));

		double altitudeDiff = Math.abs(start.getAltitude() - end.getAltitude());

		return minDuration(computedPairs, (long) altitudeDiff, startPoint, endPoint);
	}

	private CompletableFuture<Optional<Pair<Duration, Distance>>> computeDurationAsync(LatLng start, LatLng end,
			TravelMode mode) {
		return CompletableFuture.supplyAsync(() -> computeDuration(start, end, mode), THREAD_POOL);
	}

	private Optional<Pair<Duration, Distance>> computeDuration(LatLng start, LatLng end, TravelMode mode) {
		try {
			DistanceMatrix matrix = DistanceMatrixApi.newRequest(geoApiContext).mode(mode).origins(start)
					.destinations(end).await();
			List<Pair<Duration, Distance>> pairs = Arrays.stream(matrix.rows)
					.flatMap(row -> Arrays.stream(row.elements)
							.filter(element -> element.status.equals(DistanceMatrixElementStatus.OK))
							.map(element -> Pair.of(element.duration, element.distance)))
					.collect(Collectors.toList());
			return pairs.size() == 0 ? Optional.empty() : Optional.of(pairs.get(0));
		} catch (ApiException | InterruptedException | IOException e) {
			throw new RuntimeException("Computing " + mode.name().toLowerCase() + " duration failed", e);
		}
	}

	/**
	 * Compute the estimate minimum travel duration between two points give the duration and distance for each travel
	 * mode. If we have no data for the travel modes use the haversine formula, if we do have data verify the minimum
	 * distance is greater than 100 metres and get the minimum duration of these modes. However, if the minimum distance
	 * of the modes is less than 100 metres, use the altitude to calculate the minimum duration between the two points.
	 * 
	 * @param computedPairs
	 *            computed duration and distance pairs for each travel mode between the two points
	 * @param altitudeDiff
	 *            the difference in altitude
	 * @param start
	 *            start point
	 * @param end
	 *            end point
	 * @return the estimated minimum travel duration between the two points
	 */
	private Long minDuration(CompletableFuture<List<Optional<Pair<Duration, Distance>>>> computedPairs,
			long altitudeDiff, LatLng start, LatLng end) {
		try {
			List<Optional<Pair<Duration, Distance>>> pairs = computedPairs.get();
			List<Pair<Duration, Distance>> actualPairs = pairs.stream().filter(Optional::isPresent).map(Optional::get)
					.collect(Collectors.toList());

			if (actualPairs.size() > 0) {
				long minDistance = Collections
						.min(actualPairs.stream().map(pair -> pair.getSecond().inMeters).collect(Collectors.toList()));

				if (minDistance < MIN_DISTANCE_METRES) {
					return estimateFloors(altitudeDiff) * SECONDS_PER_FLOOR;
				} else {
					return Collections.min(
							actualPairs.stream().map(pair -> pair.getFirst().inSeconds).collect(Collectors.toList()));
				}
			} else {
				long planeTravelTimeSeconds = (ESTIMATED_PLANE_SPEED_METRES_PER_HR / 60) / 60;
				long haversineDistanceInMetres = (computeDistanceHaversine(start, end) * 1000);
				return haversineDistanceInMetres / planeTravelTimeSeconds;
			}
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException("Failed to compute duration", e);
		}
	}

	/**
	 * A floor is approximately 3 metres, if the altitude is less than this return one floor else estimate number of
	 * floors
	 * 
	 * @param altitude
	 *            the altitude to estimate floors from
	 * @return estimate of the number of floors
	 */
	private long estimateFloors(long altitude) {
		return altitude < METRES_PER_FLOOR ? 1 : altitude / METRES_PER_FLOOR;
	}

	/**
	 * Compute the haversine distance in KM
	 * 
	 * @param start
	 *            start point
	 * @param end
	 *            end point
	 * @return haversine distance in KM
	 */
	private long computeDistanceHaversine(LatLng start, LatLng end) {
		double latRad = Math.toRadians((end.lat - start.lat));
		double lngRad = Math.toRadians((end.lng - start.lng));

		double startLat = Math.toRadians(start.lat);
		double endLat = Math.toRadians(end.lat);

		double a = haversin(latRad) + Math.cos(startLat) * Math.cos(endLat) * haversin(lngRad);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

		return (long) (EARTH_RADIUS * c);
	}

	private double haversin(double val) {
		return Math.pow(Math.sin(val / 2), 2);
	}

}
