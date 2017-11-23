package ie.cit.teambravo.cardsec.validation;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ie.cit.teambravo.cardsec.alerts.AlertService;
import ie.cit.teambravo.cardsec.dto.Event;
import ie.cit.teambravo.cardsec.model.LatLngAlt;
import ie.cit.teambravo.cardsec.services.DurationService;
import ie.cit.teambravo.cardsec.services.EventService;
import ie.cit.teambravo.cardsec.services.PanelLocatorService;

@Service
public class ValidationServiceImpl implements ValidationService {

	private EventService eventService;
	private PanelLocatorService panelLocatorService;
	private AlertService alertService;
	private DurationService durationService;
	private Random random = new Random();

	@Autowired
	public ValidationServiceImpl(EventService eventService, PanelLocatorService panelLocatorService,
			AlertService alertService, DurationService durationService) {
		this.eventService = eventService;
		this.panelLocatorService = panelLocatorService;
		this.alertService = alertService;
		this.durationService = durationService;
	}

	@Override
	public ValidationResponse validate(String panelId, String cardId) {

		Boolean allowed = random.nextBoolean();

		// Placeholder for adding events to database
		Event event = new Event();
		event.setPanelId(panelId);
		event.setCardId(cardId);
		event.setAccessAllowed(allowed);
		event.setTimestamp(System.currentTimeMillis());
		event.setLocation(panelLocatorService.getPanelLocation(panelId));

		eventService.saveEvent(event);

		Event previousEvent = eventService.findLatestEventByCard(cardId);
		Double prevLat = previousEvent.getLocation().getCoordinates().getLatitude();
		Double prevLong = previousEvent.getLocation().getCoordinates().getLongitude();
		LatLngAlt prevDetails = new LatLngAlt(prevLat, prevLong, previousEvent.getLocation().getAltitude());
		LatLngAlt currentDetails = new LatLngAlt(event.getLocation().getCoordinates().getLatitude(),
				event.getLocation().getCoordinates().getLongitude(), event.getLocation().getAltitude());

		durationService.getTravelTimeBetween2Points(prevDetails, currentDetails);

		if (Boolean.TRUE.equals(allowed)) {
			return new ValidationResponse(event, previousEvent, Boolean.TRUE);
		}

		alertService.generateAlert(event, previousEvent);

		return new ValidationResponse(event, previousEvent, Boolean.FALSE);
	}
}
