package ie.cit.teambravo.cardsec.services;

import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.LatLng;

public interface DistanceService {
    DistanceMatrix getDistanceBetween2Points(Double startLat, Double startLng, Double endLat, Double endLng);

    DistanceMatrix getDistanceBetween2Points(LatLng start, LatLng end);
}
