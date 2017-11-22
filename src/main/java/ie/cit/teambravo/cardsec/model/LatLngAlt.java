package ie.cit.teambravo.cardsec.model;

import java.util.Objects;

public class LatLngAlt {

	private final int id;
	private final double lat;
	private final double lng;
	private final double altitude;

	public LatLngAlt(double lat, double lng, double altitude) {
		this.id = Objects.hash(lat, lng, altitude);
		this.lat = lat;
		this.lng = lng;
		this.altitude = altitude;
	}

	public double getLat() {
		return lat;
	}

	public double getLng() {
		return lng;
	}

	public double getAltitude() {
		return altitude;
	}

	public int getId() {
		return id;
	}

}
