package ie.cit.teambravo.cardsec.services;

import ie.cit.teambravo.cardsec.dto.Event;
import ie.cit.teambravo.cardsec.model.LatLngAlt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        Event eventDto = new Event();
        eventDto.setPanelId(panelId);
        eventDto.setCardId(cardId);
        eventDto.setAccessAllowed(allowed);
        eventDto.setTimestamp(System.currentTimeMillis());
        eventDto.setLocation(panelLocatorService.getPanelLocation(panelId));
        durationService.getTravelTimeBetween2Points(new LatLngAlt(36.12, -86.67, 10.0),
                new LatLngAlt(33.94, -118.40, 10.0));
        eventService.saveEvent(eventDto);

        Event previousEvent = eventService.findLatestEventByCard(cardId);
        Double prevLat = previousEvent.getLocation().getCoordinates().getLatitude();
        Double prevLong = previousEvent.getLocation().getCoordinates().getLongitude();
        LatLngAlt prevDetails = new LatLngAlt(prevLat, prevLong, previousEvent.getLocation().getAltitude());
        LatLngAlt currentDetails = new LatLngAlt(eventDto.getLocation().getCoordinates().getLatitude(), eventDto.getLocation().getCoordinates().getLongitude(), eventDto.getLocation().getAltitude());

        Long distanceBetween2Points = durationService.getTravelTimeBetween2Points(prevDetails, currentDetails);

        if (Boolean.TRUE.equals(allowed)) {
            return true;
        }

        return false;
    }
}
