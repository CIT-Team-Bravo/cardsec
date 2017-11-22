package ie.cit.teambravo.cardsec.alerts;

import ie.cit.teambravo.cardsec.dto.EventDto;

public class Alert {
	private final String severity = "High";
	private final String title = "Possible Cloned Access Card";

	private EventDto currentEvent;
	private EventDto previousEvent;

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

	public Alert(EventDto currentEvent, EventDto previousEvent) {
		this.currentEvent = currentEvent;
		this.previousEvent = previousEvent;
	}

}
