package ie.cit.teambravo.cardsec.alerts;

import ie.cit.teambravo.cardsec.dto.Event;

public interface AlertService {
	public void generateAlert(Event currentEvent, Event previousEvent);
}
