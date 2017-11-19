package ie.cit.teambravo.cardsec.services;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DistanceServiceImpl implements DistanceService {

    @Autowired
    private GeoApiContext geoApiContext;

    public DistanceMatrix getDistanceBetween2Points(Double startLat, Double startLng, Double endLat, Double endLng) {
        return getDistanceBetween2Points(new LatLng(startLat, startLng), new LatLng(endLat, endLng));
    }

    public DistanceMatrix getDistanceBetween2Points(LatLng start, LatLng end) {
        try {
            return DistanceMatrixApi.newRequest(geoApiContext)
                    .origins(start)
                    .destinations(end)
                    .mode(TravelMode.DRIVING)
                    .await();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}