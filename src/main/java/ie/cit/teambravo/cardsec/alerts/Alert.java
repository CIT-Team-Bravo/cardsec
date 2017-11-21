package ie.cit.teambravo.cardsec.alerts;

import ie.cit.teambravo.cardsec.dto.EventDto;

public class Alert {

	private EventDto currentEvent;
	private EventDto previousEvent;

	public EventDto getCurrentEvent() {
		return currentEvent;
	}

	public void setCurrentEvent(EventDto currentEvent) {
		this.currentEvent = currentEvent;
	}

	public EventDto getPreviousEvent() {
		return previousEvent;
	}

	public void setPreviousEvent(EventDto previousEvent) {
		this.previousEvent = previousEvent;
	}

	public Alert(EventDto currentEvent, EventDto previousEvent) {
		this.currentEvent = currentEvent;
		this.previousEvent = previousEvent;
	}

}
