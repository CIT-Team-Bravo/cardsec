package ie.cit.teambravo.cardsec.validation;

import ie.cit.teambravo.cardsec.events.Event;

public class ValidationResponse {
	private final String reason;

	private Event currentEvent;
	private Event previousEvent;

	private Boolean validEvent;

	public ValidationResponse(Event currentEvent, Event previousEvent, Boolean validEvent) {
		this.currentEvent = currentEvent;
		this.previousEvent = previousEvent;
		this.validEvent = validEvent;
		this.reason = validEvent ? "Possible time-distance event." : "Impossible time-distance event.";
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
