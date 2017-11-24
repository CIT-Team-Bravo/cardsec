package ie.cit.teambravo.cardsec.validation;

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

	@Autowired
	public ValidationServiceImpl(EventService eventService, PanelLocatorService panelLocatorService,
			AlertService alertService, DurationService durationService) {
		this.eventService = eventService;
		this.panelLocatorService = panelLocatorService;
		this.alertService = alertService;
		this.durationService = durationService;
	}

	@Override
	public ValidationResponse validate(String panelId, String cardId, Boolean allowed) {

		Event event = new Event();
		event.setPanelId(panelId);
		event.setCardId(cardId);
		event.setAccessAllowed(allowed);
		event.setTimestamp(System.currentTimeMillis());
		try {
			event.setLocation(panelLocatorService.getPanelLocation(panelId));
		} catch (Exception e) {
			throw new IllegalArgumentException("Unknown panelId " + panelId, e);
		}
		eventService.saveEvent(event);

		Event previousEvent = eventService.findLatestEventByCard(cardId);

		if (previousEvent == null) {
			return new ValidationResponse(event, null, Boolean.TRUE);
		}

		long timeBetweenEvents = event.getTimestamp() - previousEvent.getTimestamp();
		long minimumJourneyDuration = durationService.getTravelTimeBetween2Points(getLatLngAlt(event),
				getLatLngAlt(previousEvent));
		if (minimumJourneyDuration < timeBetweenEvents) {
			return new ValidationResponse(event, previousEvent, Boolean.TRUE);
		} else {
			alertService.generateAlert(event, previousEvent);
			return new ValidationResponse(event, previousEvent, Boolean.FALSE);
		}
	}

	private LatLngAlt getLatLngAlt(Event event) {
		return new LatLngAlt(event.getLocation().getCoordinates().getLatitude(),
				event.getLocation().getCoordinates().getLongitude(), event.getLocation().getAltitude());

	}

}
