package ie.cit.teambravo.cardsec.services;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.stefanbirkner.fishbowl.Fishbowl;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.Distance;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElement;
import com.google.maps.model.DistanceMatrixRow;
import com.google.maps.model.Duration;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;

import ie.cit.teambravo.cardsec.duration.DurationServiceImpl;
import ie.cit.teambravo.cardsec.location.LatLngAlt;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
		GeocodingApi.class, DistanceMatrixApiRequest.class, DistanceMatrixApi.class
})
public class DurationServiceImplTests {

	@Mock
	private GeoApiContext geoApiContext;

	@InjectMocks
	private DurationServiceImpl distanceService;

	@Test
	public void should_get_travel_duration_between_2_points() throws Exception {
		// Arrange
		LatLng start = new LatLng(14, 22);
		LatLng end = new LatLng(15, 10);
		LatLngAlt start3d = new LatLngAlt(start.lat, start.lng, 10);
		LatLngAlt end3d = new LatLngAlt(end.lat, end.lng, 10);

		Duration computedDuration = new Duration();
		computedDuration.inSeconds = 1000;

		DistanceMatrixElement element = new DistanceMatrixElement();
		element.duration = computedDuration;

		DistanceMatrixRow distanceMatrixRow = new DistanceMatrixRow();
		distanceMatrixRow.elements = new DistanceMatrixElement[] {
				element
		};
		DistanceMatrixRow[] distanceMatrixRows = new DistanceMatrixRow[] {
				distanceMatrixRow
		};
		String[] startLoc = new String[] {
				"address start"
		};
		String[] endLoc = new String[] {
				"address end"
		};
		DistanceMatrix distanceMatrix = new DistanceMatrix(startLoc, endLoc, distanceMatrixRows);

		DistanceMatrixApiRequest distanceMatrixApiRequest = mock(DistanceMatrixApiRequest.class);
		mockStatic(DistanceMatrixApi.class);
		when(distanceMatrixApiRequest.origins(any(LatLng.class))).thenReturn(distanceMatrixApiRequest);
		when(distanceMatrixApiRequest.destinations(any(LatLng.class))).thenReturn(distanceMatrixApiRequest);
		when(distanceMatrixApiRequest.mode(TravelMode.DRIVING)).thenReturn(distanceMatrixApiRequest);
		when(distanceMatrixApiRequest.mode(TravelMode.WALKING)).thenReturn(distanceMatrixApiRequest);
		when(distanceMatrixApiRequest.mode(TravelMode.BICYCLING)).thenReturn(distanceMatrixApiRequest);
		when(distanceMatrixApiRequest.await()).thenReturn(distanceMatrix);

		when(DistanceMatrixApi.newRequest(geoApiContext)).thenReturn(distanceMatrixApiRequest);

		// Act
		Long duration = distanceService.getTravelTimeBetween2Points(start3d, end3d);

		// Assert
		assertNotNull(duration);
		assertThat(duration, is(element.duration.inSeconds));
	}

	@Test
	public void should_return_minimum_duration_of_travel_modes() throws Exception {
		// Arrange
		LatLng start = new LatLng(14, 22);
		LatLng end = new LatLng(15, 10);
		LatLngAlt start3d = new LatLngAlt(start.lat, start.lng, 10000);
		LatLngAlt end3d = new LatLngAlt(end.lat, end.lng, 10);
		long nonDrivingDuration = 1000L;
		long drivingDuration = 100L;

		DistanceMatrix drivingDistanceMatrix = buildDistanceMatrix(drivingDuration);
		DistanceMatrix nonDrivingDistanceMatrix = buildDistanceMatrix(nonDrivingDuration);

		DistanceMatrixApiRequest drivingDistanceRequest = setupApiRequestMock(mock(DistanceMatrixApiRequest.class),
				TravelMode.DRIVING, drivingDistanceMatrix);
		DistanceMatrixApiRequest walkingDistanceRequest = setupApiRequestMock(mock(DistanceMatrixApiRequest.class),
				TravelMode.WALKING, nonDrivingDistanceMatrix);
		DistanceMatrixApiRequest bikingDistanceRequest = setupApiRequestMock(mock(DistanceMatrixApiRequest.class),
				TravelMode.BICYCLING, nonDrivingDistanceMatrix);

		mockStatic(DistanceMatrixApi.class);
		when(DistanceMatrixApi.newRequest(geoApiContext)).thenReturn(drivingDistanceRequest);
		when(DistanceMatrixApi.newRequest(geoApiContext).mode(TravelMode.WALKING)).thenReturn(walkingDistanceRequest);
		when(DistanceMatrixApi.newRequest(geoApiContext).mode(TravelMode.BICYCLING)).thenReturn(bikingDistanceRequest);

		// Act
		Long duration = distanceService.getTravelTimeBetween2Points(start3d, end3d);

		// Assert
		assertNotNull(duration);
		assertThat(duration, is(drivingDuration));
	}

	@Test
	public void should_throw_exception_if_computing_duration_fails() throws Exception {
		// Arrange
		LatLng start = new LatLng(14, 22);
		LatLng end = new LatLng(15, 10);
		LatLngAlt start3d = new LatLngAlt(start.lat, start.lng, 10);
		LatLngAlt end3d = new LatLngAlt(end.lat, end.lng, 10);
		long nonDrivingDuration = 1000L;
		long drivingDuration = 100L;

		DistanceMatrix drivingDistanceMatrix = buildDistanceMatrix(drivingDuration);
		DistanceMatrix nonDrivingDistanceMatrix = buildDistanceMatrix(nonDrivingDuration);

		DistanceMatrixApiRequest drivingDistanceRequest = setupApiRequestMock(mock(DistanceMatrixApiRequest.class),
				TravelMode.DRIVING, drivingDistanceMatrix);
		DistanceMatrixApiRequest walkingDistanceRequest = setupApiRequestMock(mock(DistanceMatrixApiRequest.class),
				TravelMode.WALKING, nonDrivingDistanceMatrix);
		DistanceMatrixApiRequest bikingDistanceRequest = setupApiRequestMock(mock(DistanceMatrixApiRequest.class),
				TravelMode.BICYCLING, nonDrivingDistanceMatrix);

		mockStatic(DistanceMatrixApi.class);
		when(DistanceMatrixApi.newRequest(geoApiContext)).thenReturn(drivingDistanceRequest);
		when(DistanceMatrixApi.newRequest(geoApiContext).mode(TravelMode.WALKING)).thenReturn(walkingDistanceRequest);
		when(DistanceMatrixApi.newRequest(geoApiContext).mode(TravelMode.BICYCLING)).thenReturn(bikingDistanceRequest);
		when(drivingDistanceRequest.await()).thenThrow(new IOException());

		// Act
		Throwable exception = Fishbowl.exceptionThrownBy(() -> {
			distanceService.getTravelTimeBetween2Points(start3d, end3d);
		});

		// Assert
		assertEquals(RuntimeException.class, exception.getClass());
		assertEquals("Failed to compute duration", exception.getMessage());
		assertEquals("java.lang.RuntimeException: Computing driving duration failed",
				exception.getCause().getLocalizedMessage());
	}

	private DistanceMatrix buildDistanceMatrix(long duration) throws InterruptedException, ApiException, IOException {
		Duration computedDuration = new Duration();
		computedDuration.inSeconds = duration;
		DistanceMatrixElement element = new DistanceMatrixElement();
		element.duration = computedDuration;
		Distance computedDistance = new Distance();
		computedDistance.inMeters = 100000;
		DistanceMatrixRow distanceMatrixRow = new DistanceMatrixRow();
		distanceMatrixRow.elements = new DistanceMatrixElement[] {
				element
		};
		DistanceMatrixRow[] distanceMatrixRows = new DistanceMatrixRow[] {
				distanceMatrixRow
		};
		String[] startLoc = new String[] {
				"address start"
		};
		String[] endLoc = new String[] {
				"address end"
		};

		return new DistanceMatrix(startLoc, endLoc, distanceMatrixRows);
	}

	private DistanceMatrixApiRequest setupApiRequestMock(DistanceMatrixApiRequest request, TravelMode mode,
			DistanceMatrix matrix) throws InterruptedException, ApiException, IOException {
		when(request.origins(any(LatLng.class))).thenReturn(request);
		when(request.destinations(any(LatLng.class))).thenReturn(request);
		when(request.mode(mode)).thenReturn(request);
		when(request.await()).thenReturn(matrix);
		return request;
	}
}
