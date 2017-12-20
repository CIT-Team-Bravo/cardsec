package ie.cit.teambravo.cardsec.alerts;

import ie.cit.teambravo.cardsec.events.Event;

/**
 * This is the API to the alerts package. It deals with generating alerts for suspicious swipes
 */
public interface AlertService {
	/**
	 * Generate an alert
	 * 
	 * @param currentEvent
	 *            the event that triggered the alert
	 * @param previousEvent
	 *            the previous event whose time and location makes the current alert suspicious
	 */
	public void generateAlert(Event currentEvent, Event previousEvent);
}
