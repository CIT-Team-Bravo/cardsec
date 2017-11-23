package ie.cit.teambravo.cardsec.services;

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
import org.springframework.stereotype.Service;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;

import ie.cit.teambravo.cardsec.model.LatLngAlt;

@Service
public class DurationServiceImpl implements DurationService {

	private static final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(3);
	private GeoApiContext geoApiContext;

	@Autowired
	public DurationServiceImpl(GeoApiContext geoApiContext) {
		this.geoApiContext = geoApiContext;
	}

	@Cacheable(value = "travelDuration", key = "{#start.getId(), #end.getId()}")
	public Long getTravelTimeBetween2Points(LatLngAlt start, LatLngAlt end) {
		LatLng startPoint = new LatLng(start.getLat(), start.getLng());
		LatLng endPoint = new LatLng(end.getLat(), end.getLng());

		List<CompletableFuture<Long>> durationCalculations = Stream
				.of(TravelMode.DRIVING, TravelMode.BICYCLING, TravelMode.WALKING)
				.map(mode -> computeDurationAsync(startPoint, endPoint, mode)).collect(Collectors.toList());

		CompletableFuture<List<Long>> computedDistanceTimes = CompletableFuture
				.allOf(durationCalculations.toArray(new CompletableFuture[durationCalculations.size()]))
				.thenApply(ignored -> durationCalculations.stream().map(CompletableFuture::join)
						.collect(Collectors.toList()));

		durationCalculations.forEach(calculation -> calculation.whenComplete((ignored, exception) -> {
			if (exception != null) {
				computedDistanceTimes.completeExceptionally(exception);
			}
		}));

		Double altitudeDiff = Math.abs(start.getAltitude() - end.getAltitude());

		return minDuration(computedDistanceTimes, altitudeDiff.longValue());
	}

	private CompletableFuture<Long> computeDurationAsync(LatLng start, LatLng end, TravelMode mode) {
		return CompletableFuture.supplyAsync(() -> computeDuration(start, end, mode), THREAD_POOL);
	}

	private Long computeDuration(LatLng start, LatLng end, TravelMode mode) {
		try {
			DistanceMatrix matrix = DistanceMatrixApi.newRequest(geoApiContext).mode(mode).origins(start)
					.destinations(end).await();

			List<Long> travelInfo = Arrays.stream(matrix.rows)
					.flatMap(row -> Arrays.stream(row.elements).map(element -> element.duration.inSeconds))
					.collect(Collectors.toList());

			return travelInfo.get(0);
		} catch (ApiException | InterruptedException | IOException e) {
			throw new RuntimeException("Computing " + mode.name().toLowerCase() + " duration failed", e);
		}
	}

	private Long minDuration(CompletableFuture<List<Long>> computedDurations, Long altitudeInMetres) {
		try {
			List<Long> durations = computedDurations.get();
			return Collections.min(durations);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to compute duration", e);
		}
	}

}
