package ie.cit.teambravo.cardsec.alerts;

import ie.cit.teambravo.cardsec.events.Event;

public class Alert {
	private static final String SEVERITY = "High";
	private static final String TITLE = "Possible Cloned Access Card";
	private static final String DESCRIPTION = "An access-card has been used that was very recently used in another location, indicating that it is unlikely to be the same card-holder";

	private Event currentEvent;
	private Event previousEvent;

	public Alert(Event currentEvent, Event previousEvent) {
		this.currentEvent = currentEvent;
		this.previousEvent = previousEvent;
	}

	public String getSeverity() {
		return SEVERITY;
	}

	public String getTitle() {
		return TITLE;
	}

	public Event getCurrentEvent() {
		return currentEvent;
	}

	public Event getPreviousEvent() {
		return previousEvent;
	}

	public String getDescription() {
		return DESCRIPTION;
	}
}
