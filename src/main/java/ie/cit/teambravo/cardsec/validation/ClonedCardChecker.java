package ie.cit.teambravo.cardsec.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ie.cit.teambravo.cardsec.alerts.AlertService;
import ie.cit.teambravo.cardsec.duration.DurationService;
import ie.cit.teambravo.cardsec.events.Event;
import ie.cit.teambravo.cardsec.events.EventService;
import ie.cit.teambravo.cardsec.location.LatLngAlt;
import ie.cit.teambravo.cardsec.location.PanelLocatorService;

@Service
public class ClonedCardChecker implements ValidationService {

	private static Logger LOGGER = LoggerFactory.getLogger(ClonedCardChecker.class);
	private EventService eventService;
	private PanelLocatorService panelLocatorService;
	private AlertService alertService;
	private DurationService durationService;

	@Autowired
	public ClonedCardChecker(EventService eventService, PanelLocatorService panelLocatorService,
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

		Event previousEvent = eventService.findLatestEventByCard(cardId);

		eventService.saveEvent(event);

		if (previousEvent == null) {
			LOGGER.info("Swipe on {} at {}, no previous swipe. Request is OK");

			return new ValidationResponse(event, null, Boolean.TRUE);
		}

		long timeBetweenEvents = event.getTimestamp() - previousEvent.getTimestamp();
		long minimumJourneyDuration = durationService.getTravelTimeBetween2Points(getLatLngAlt(event),
				getLatLngAlt(previousEvent)) * 1000;

		boolean validationResult = minimumJourneyDuration < timeBetweenEvents;

		LOGGER.info(
				"Card {} swiped on panel {} ({}) at {} , previous swipe on {} ({}) at {}. Time between swipes {}. Minimum journey time {}. Request is {}",
				cardId, panelId, event.getLocation().getRelativeLocation(), event.getTimestamp(),
				previousEvent.getPanelId(), previousEvent.getLocation().getRelativeLocation(),
				previousEvent.getTimestamp(), timeBetweenEvents, minimumJourneyDuration,
				validationResult ? "OK" : "ALERT");

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
