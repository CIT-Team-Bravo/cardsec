package ie.cit.teambravo.cardsec.dto;

import com.google.maps.model.LatLng;

public class LatLngAlt {
    private final LatLng latLng;

    private final double altitude;

    public LatLngAlt(LatLng latLng, double altitude) {
        this.latLng = latLng;
        this.altitude = altitude;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public double getAltitude() {
        return altitude;
    }
}
