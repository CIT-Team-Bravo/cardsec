package ie.cit.teambravo.cardsec.dto;

import java.util.Objects;

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

	@Override
	public final boolean equals(Object other) {
		if (other == this)
			return true;
		if (!(other instanceof LatLngAlt))
			return false;

		LatLngAlt otherLatLngAlt = (LatLngAlt) other;
		return (latLng != null && otherLatLngAlt.latLng != null)
                && Objects.equals(latLng.lat, otherLatLngAlt.latLng.lat)
				&& Objects.equals(latLng.lng, otherLatLngAlt.latLng.lng)
				&& Objects.equals(altitude, otherLatLngAlt.altitude)
                || (latLng == null && otherLatLngAlt.latLng == null)
                && Objects.equals(altitude, otherLatLngAlt.altitude);
	}

	@Override
	public final int hashCode() {
		return latLng != null ? Objects.hash(latLng.lat, latLng.lng, altitude) : 0;
	}

}
