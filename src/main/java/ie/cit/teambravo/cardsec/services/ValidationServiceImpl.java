package ie.cit.teambravo.cardsec.services;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;

import ie.cit.teambravo.cardsec.dto.EventDto;
import ie.cit.teambravo.cardsec.messaging.EventGateway;

@Service
public class ValidationServiceImpl implements ValidationService {

	private EventService eventService;
	private PanelLocatorService panelLocatorService;
	EventGateway eventGateway;

	@Autowired
	public ValidationServiceImpl(EventService eventService, PanelLocatorService panelLocatorService,
			EventGateway eventGateway) {
		this.eventService = eventService;
		this.panelLocatorService = panelLocatorService;
		this.eventGateway = eventGateway;
	}

	@Override
	public boolean validate(String panelId, String cardId, Boolean allowed) {

		// Placeholder for adding events to database
		EventDto eventDto = new EventDto();
		eventDto.setPanelId(panelId);
		eventDto.setCardId(cardId);
		eventDto.setAccessAllowed(allowed);
		eventDto.setTimestamp(new Date().toString());
		eventDto.setLocationDto(panelLocatorService.getPanelLocation(panelId));

		eventService.saveEvent(eventDto);

		if (Boolean.TRUE.equals(allowed)) {
			return true;
		}

		eventGateway.sendToMqtt(new GenericMessage<String>("BLAH"));

		return false;
	}
}
