package ie.cit.teambravo.cardsec.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ie.cit.teambravo.cardsec.dto.EventDto;

@Service
public class ValidationServiceImpl implements ValidationService {

	private EventService eventService;
	private PanelLocatorService panelLocatorService;

	@Autowired
	public ValidationServiceImpl(EventService eventService, PanelLocatorService panelLocatorService) {
		this.eventService = eventService;
		this.panelLocatorService = panelLocatorService;
	}

	@Override
	public boolean validate(String panelId, String cardId, Boolean allowed) {

		// Placeholder for adding events to database
		EventDto eventDto = new EventDto();
		eventDto.setPanelId(panelId);
		eventDto.setCardId(cardId);
		eventDto.setAccessAllowed(allowed);
		eventDto.setTimestamp(System.currentTimeMillis());
		eventDto.setLocationDto(panelLocatorService.getPanelLocation(panelId));

		eventService.saveEvent(eventDto);

		if (Boolean.TRUE.equals(allowed)) {
			return true;
		}

		return false;
	}
}
