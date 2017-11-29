package ie.cit.teambravo.cardsec.duration;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
import com.google.maps.model.Duration;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;

import ie.cit.teambravo.cardsec.location.LatLngAlt;

@Service
public class DurationServiceImpl implements DurationService {
	private static final long MIN_DISTANCE = 100L;
	private static final long SECONDS_PER_FLOOR = 8L;
	private static final long FEET_PER_FLOOR = 10L;
	private static final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(3);
	private final GeoApiContext geoApiContext;

	@Autowired
	public DurationServiceImpl(GeoApiContext geoApiContext) {
		this.geoApiContext = geoApiContext;
	}

	@Cacheable(value = "travelDuration", key = "{#start.getId(), #end.getId()}")
	public Long getTravelTimeBetween2Points(LatLngAlt start, LatLngAlt end) {
		LatLng startPoint = new LatLng(start.getLat(), start.getLng());
		LatLng endPoint = new LatLng(end.getLat(), end.getLng());

		List<CompletableFuture<Pair<Duration, Distance>>> durationCalculations = Stream
				.of(TravelMode.DRIVING, TravelMode.BICYCLING, TravelMode.WALKING)
				.map(mode -> computeDurationAsync(startPoint, endPoint, mode)).collect(Collectors.toList());

		CompletableFuture<List<Pair<Duration, Distance>>> computedPairs = CompletableFuture
				.allOf(durationCalculations.toArray(new CompletableFuture[durationCalculations.size()]))
				.thenApply(ignored -> durationCalculations.stream().map(CompletableFuture::join)
						.collect(Collectors.toList()));

		durationCalculations.forEach(calculation -> calculation.whenComplete((ignored, exception) -> {
			if (exception != null) {
				computedPairs.completeExceptionally(exception);
			}
		}));

		double altitudeDiff = Math.abs(start.getAltitude() - end.getAltitude());

		return minDuration(computedPairs, (long) altitudeDiff);
	}

	private CompletableFuture<Pair<Duration, Distance>> computeDurationAsync(LatLng start, LatLng end,
			TravelMode mode) {
		return CompletableFuture.supplyAsync(() -> computeDuration(start, end, mode), THREAD_POOL);
	}

	private Pair<Duration, Distance> computeDuration(LatLng start, LatLng end, TravelMode mode) {
		try {
			DistanceMatrix matrix = DistanceMatrixApi.newRequest(geoApiContext).mode(mode).origins(start)
					.destinations(end).await();

			List<Pair<Duration, Distance>> pairs = Arrays.stream(matrix.rows).flatMap(
					row -> Arrays.stream(row.elements).map(element -> Pair.of(element.duration, element.distance)))
					.collect(Collectors.toList());

			return pairs.get(0);
		} catch (ApiException | InterruptedException | IOException e) {
			throw new RuntimeException("Computing " + mode.name().toLowerCase() + " duration failed", e);
		}
	}

	private Long minDuration(CompletableFuture<List<Pair<Duration, Distance>>> computedPairs, long altitudeDiff) {
		try {
			List<Pair<Duration, Distance>> pairs = computedPairs.get();
			long minDistance = Collections
					.min(pairs.stream().map(pair -> pair.getSecond().inMeters).collect(Collectors.toList()));

			if (minDistance < MIN_DISTANCE) {
				return estimateFloors(altitudeDiff) * SECONDS_PER_FLOOR;
			} else {
				return Collections
						.min(pairs.stream().map(pair -> pair.getFirst().inSeconds).collect(Collectors.toList()));
			}
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException("Failed to compute duration", e);
		}
	}

	private long estimateFloors(long altitude) {
		return altitude < 10 ? 1 : altitude / FEET_PER_FLOOR;
	}

}
