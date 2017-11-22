package ie.cit.teambravo.cardsec.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ie.cit.teambravo.cardsec.alerts.AlertService;
import ie.cit.teambravo.cardsec.dto.Event;
import ie.cit.teambravo.cardsec.model.LatLngAlt;

@Service
public class ValidationServiceImpl implements ValidationService {

	private EventService eventService;
	private PanelLocatorService panelLocatorService;
	private AlertService alertService;
	private DurationService durationService;

	@Autowired
	public ValidationServiceImpl(EventService eventService, PanelLocatorService panelLocatorService,
			AlertService alertService, DurationService durationService) {
		this.eventService = eventService;
		this.panelLocatorService = panelLocatorService;
		this.alertService = alertService;
		this.durationService = durationService;
	}

	@Override
	public boolean validate(String panelId, String cardId, Boolean allowed) {

		// Placeholder for adding events to database
		Event event = new Event();
		event.setPanelId(panelId);
		event.setCardId(cardId);
		event.setAccessAllowed(allowed);
		event.setTimestamp(System.currentTimeMillis());
		event.setLocation(panelLocatorService.getPanelLocation(panelId));

		durationService.getTravelTimeBetween2Points(new LatLngAlt(36.12, -86.67, 10.0),
				new LatLngAlt(33.94, -118.40, 10.0));

		eventService.saveEvent(event);

		if (Boolean.TRUE.equals(allowed)) {
			return true;
		}

		alertService.generateAlert(event, event);

		return false;
	}
}
