package ie.cit.teambravo.cardsec.alerts;

import ie.cit.teambravo.cardsec.dto.EventDto;

public class Alert {
	private final String severity = "High";
	private final String title = "Possible Cloned Access Card";
	private final String description = "An access-card has been used that was very recently used in another location, indicating that it is unlikely to be the same card-holder";

	private EventDto currentEvent;
	private EventDto previousEvent;

	public Alert(EventDto currentEvent, EventDto previousEvent) {
		this.currentEvent = currentEvent;
		this.previousEvent = previousEvent;
	}

	public String getSeverity() {
		return severity;
	}

	public String getTitle() {
		return title;
	}

	public EventDto getCurrentEvent() {
		return currentEvent;
	}

	public EventDto getPreviousEvent() {
		return previousEvent;
	}

	public String getDescription() {
		return description;
	}

}
