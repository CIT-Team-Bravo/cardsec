package ie.cit.teambravo.cardsec.alerts;

import ie.cit.teambravo.cardsec.dto.Event;

public class Alert {
	private static final String severity = "High";
	private static final String title = "Possible Cloned Access Card";
	private static final String description = "An access-card has been used that was very recently used in another location, indicating that it is unlikely to be the same card-holder";

	private Event currentEvent;
	private Event previousEvent;

	public Alert(Event currentEvent, Event previousEvent) {
		this.currentEvent = currentEvent;
		this.previousEvent = previousEvent;
	}

	public String getSeverity() {
		return severity;
	}

	public String getTitle() {
		return title;
	}

	public Event getCurrentEvent() {
		return currentEvent;
	}

	public Event getPreviousEvent() {
		return previousEvent;
	}

	public String getDescription() {
		return description;
	}
}
