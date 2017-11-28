package ie.cit.teambravo.cardsec.alerts;

import ie.cit.teambravo.cardsec.events.Event;

public interface AlertService {
	public void generateAlert(Event currentEvent, Event previousEvent);
}
