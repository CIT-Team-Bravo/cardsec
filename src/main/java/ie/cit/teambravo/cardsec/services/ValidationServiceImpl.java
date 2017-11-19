package ie.cit.teambravo.cardsec.services;

import ie.cit.teambravo.cardsec.dto.EventDto;
import ie.cit.teambravo.cardsec.dto.LocationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ValidationServiceImpl implements ValidationService {

    @Autowired
    private EventService eventService;

    @Override
    public boolean validate(String panelId, String cardId, Boolean allowed) {

        // Placeholder for adding events to database
        EventDto eventDto = new EventDto();
        eventDto.setPanelId(panelId);
        eventDto.setCardId(cardId);
        eventDto.setAccessAllowed(allowed);
        eventDto.setTimestamp(new Date().toString());

        eventService.saveEvent(eventDto);

        if (Boolean.TRUE.equals(allowed)) {
            return true;
        }

        return false;
    }
}
