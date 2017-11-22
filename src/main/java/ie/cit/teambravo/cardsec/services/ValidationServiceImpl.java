package ie.cit.teambravo.cardsec.services;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.maps.model.LatLng;

import ie.cit.teambravo.cardsec.dto.EventDto;
import ie.cit.teambravo.cardsec.dto.LatLngAlt;

@Service
public class ValidationServiceImpl implements ValidationService {
    
	private EventService eventService;

	private PanelLocatorService panelLocatorService;

	private DurationService durationService;

	@Autowired
	public ValidationServiceImpl(EventService eventService, PanelLocatorService panelLocatorService,
			DurationService durationService) {
	    this.eventService = eventService;
	    this.panelLocatorService = panelLocatorService;
	    this.durationService = durationService;
	}

	@Override
	public boolean validate(String panelId, String cardId, Boolean allowed) {

		// Placeholder for adding events to database
		EventDto eventDto = new EventDto();
		eventDto.setPanelId(panelId);
		eventDto.setCardId(cardId);
		eventDto.setAccessAllowed(allowed);
		eventDto.setTimestamp(new Date().toString());
		// eventDto.setLocationDto(panelLocatorService.getPanelLocation(panelId));
		durationService.getTravelTimeBetween2Points(new LatLngAlt(36.12, -86.67, 10.0),
				new LatLngAlt(33.94, -118.40, 10.0));
		eventService.saveEvent(eventDto);

		if (Boolean.TRUE.equals(allowed)) {
			return true;
		}

		return false;
	}
}
