package ie.cit.teambravo.cardsec.services;

import com.google.maps.model.DistanceMatrix;
import ie.cit.teambravo.cardsec.dto.EventDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ValidationServiceImpl implements ValidationService {

    @Autowired
    private EventService eventService;
    @Autowired
    private DistanceService distanceService;

    @Autowired
    private PanelLocatorService panelLocatorService;

    @Override
    public boolean validate(String panelId, String cardId, Boolean allowed) {

        // Placeholder for adding events to database
        EventDto eventDto = new EventDto();
        eventDto.setPanelId(panelId);
        eventDto.setCardId(cardId);
        eventDto.setAccessAllowed(allowed);
        eventDto.setTimestamp(new Date());
        eventDto.setLocationDto(panelLocatorService.getPanelLocation(panelId));
        eventService.saveEvent(eventDto);

        EventDto previousEvent = eventService.findLatestEventByCard(cardId);
        Double prevLat = previousEvent.getLocationDto().getCoordinates().getLatitude();
        Double prevLong = previousEvent.getLocationDto().getCoordinates().getLongitude();

        DistanceMatrix distanceBetween2Points = distanceService.getDistanceBetween2Points(prevLat, prevLong, eventDto.getLocationDto().getCoordinates().getLatitude(), eventDto.getLocationDto().getCoordinates().getLongitude());
        
        if (Boolean.TRUE.equals(allowed)) {
            return true;
        }

        return false;
    }
}
