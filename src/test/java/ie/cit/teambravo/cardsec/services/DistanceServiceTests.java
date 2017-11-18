package ie.cit.teambravo.cardsec.services;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixRow;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({GeocodingApi.class, DistanceMatrixApiRequest.class, DistanceMatrixApi.class})
public class DistanceServiceTests {

    @Mock
    private GeoApiContext geoApiContext;

    @InjectMocks
    private DistanceService distanceService;

    @Test
    public void should_get_distance_between_2_points() throws Exception {
        // Arrange
        LatLng start = new LatLng(14, 22);
        LatLng end = new LatLng(15, 10);
        DistanceMatrixRow distanceMatrixRow = new DistanceMatrixRow();
        DistanceMatrixRow[] distanceMatrixRows = new DistanceMatrixRow[]{distanceMatrixRow};
        String[] startLoc = new String[]{"address start"};
        String[] endLoc = new String[]{"address end"};

        DistanceMatrix distanceMatrix = new DistanceMatrix(startLoc, endLoc, distanceMatrixRows);

        DistanceMatrixApiRequest distanceMatrixApiRequest = mock(DistanceMatrixApiRequest.class);
        mockStatic(DistanceMatrixApi.class);
        when(distanceMatrixApiRequest.origins(start)).thenReturn(distanceMatrixApiRequest);
        when(distanceMatrixApiRequest.destinations(end)).thenReturn(distanceMatrixApiRequest);
        when(distanceMatrixApiRequest.mode(TravelMode.DRIVING)).thenReturn(distanceMatrixApiRequest);
        when(distanceMatrixApiRequest.await()).thenReturn(distanceMatrix);

        when(DistanceMatrixApi.newRequest(geoApiContext)).thenReturn(distanceMatrixApiRequest);

        // Act
        DistanceMatrix matrix = distanceService.getDistanceBetween2Points(start, end);

        // Assert
        assertNotNull(matrix);
        assertThat(matrix, is(distanceMatrix));
    }
}
