package ie.cit.teambravo.cardsec.services;

import ie.cit.teambravo.cardsec.dto.LatLngAlt;

public interface DurationService {

	/**
	 * Get the travel time in seconds between two given points.
	 * 
	 * @param start
	 *            the start position
	 * @param end
	 *            the end position
	 * @return the duration in seconds to travel between the two positions
	 */
	Long getTravelTimeBetween2Points(LatLngAlt start, LatLngAlt end);
}
