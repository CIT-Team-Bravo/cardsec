package ie.cit.teambravo.cardsec.validation;

import ie.cit.teambravo.cardsec.dto.Event;

public class ValidationResponse {
	private final String reason = "Impossible time-distance event.";

	private Event currentEvent;
	private Event previousEvent;

	private Boolean validEvent;

	public ValidationResponse(Event currentEvent, Event previousEvent, Boolean validEvent) {
		this.currentEvent = currentEvent;
		this.previousEvent = previousEvent;
		this.validEvent = validEvent;
	}

	public String getReason() {
		return reason;
	}

	public Event getCurrentEvent() {
		return currentEvent;
	}

	public Event getPreviousEvent() {
		return previousEvent;
	}

	public Boolean getValidEvent() {
		return validEvent;
	}

}
