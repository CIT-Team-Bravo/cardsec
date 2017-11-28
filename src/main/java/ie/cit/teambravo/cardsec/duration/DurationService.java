package ie.cit.teambravo.cardsec.duration;

import ie.cit.teambravo.cardsec.location.LatLngAlt;

public interface DurationService {

	/**
	 * Get the estimated minimum travel time in seconds between two given points. Note that this get's more lenient as
	 * travel times decrease.
	 * 
	 * @param start
	 *            the start position
	 * @param end
	 *            the end position
	 * @return the duration in seconds to travel between the two positions
	 */
	Long getTravelTimeBetween2Points(LatLngAlt start, LatLngAlt end);
}
