package ie.cit.teambravo.cardsec.alerts;

import ie.cit.teambravo.cardsec.dto.EventDto;

public interface AlertService {
	public void generateAlert(EventDto currentEvent, EventDto previousEvent);
}
