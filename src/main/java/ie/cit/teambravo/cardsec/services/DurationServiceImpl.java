package ie.cit.teambravo.cardsec.services;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.Duration;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import ie.cit.teambravo.cardsec.dto.LatLngAlt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

@Service
public class DurationServiceImpl implements DurationService {

    private static final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(3);
    private GeoApiContext geoApiContext;

    @Autowired
    public DurationServiceImpl(GeoApiContext geoApiContext) {
        this.geoApiContext = geoApiContext;
    }

    public Long getTravelTimeBetween2Points(LatLngAlt startPosition, LatLngAlt endPosition) {
        List<CompletableFuture<Long>> durationCalculations =
                Stream.of(TravelMode.DRIVING, TravelMode.BICYCLING, TravelMode.WALKING).
                        map(mode -> computeDurationAsync(startPosition.getLatLng(), endPosition.getLatLng(), mode))
                        .collect(Collectors.toList());

        CompletableFuture<List<Long>> computedDurations =
                CompletableFuture.allOf(durationCalculations.toArray(new CompletableFuture[durationCalculations.size()]))
                        .thenApply(ignored -> durationCalculations.stream()
                                .map(CompletableFuture::join)
                                .collect(Collectors.toList()));

        durationCalculations.forEach(calculation -> calculation.whenComplete((ignored, exception) -> {
            if (exception != null) {
                computedDurations.completeExceptionally(exception);
            }
        }));

        return minDuration(computedDurations);
    }

    private CompletableFuture<Long> computeDurationAsync(LatLng start, LatLng end, TravelMode mode) {
        return CompletableFuture.supplyAsync(() -> computeDuration(start, end, mode), THREAD_POOL);
    }

    private Long computeDuration(LatLng start, LatLng end, TravelMode mode) {
        try {
            DistanceMatrix matrix = DistanceMatrixApi.newRequest(geoApiContext)
                    .mode(mode)
                    .origins(start)
                    .destinations(end)
                    .await();

            List<Duration> durations = Arrays.stream(matrix.rows)
                    .flatMap(row -> Arrays.stream(row.elements)
                            .map(element -> element.duration))
                    .collect(Collectors.toList());

            return durations.get(0).inSeconds;
        } catch (ApiException | InterruptedException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Computing " + mode.name().toLowerCase() + " duration failed");
        }
    }

    private Long minDuration(CompletableFuture<List<Long>> computedDuration) {
        try {
            List<Long> durations = computedDuration.get();
            return Collections.min(durations);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to compute duration", e);
        }
    }
}