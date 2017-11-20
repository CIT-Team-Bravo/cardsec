package ie.cit.teambravo.cardsec.services;

import ie.cit.teambravo.cardsec.dto.EventDto;

import java.util.List;

public interface EventService {
    EventDto saveEvent(EventDto eventDto);

    List<EventDto> findByCardId(String cardId);
    EventDto findLatestEventByCard(String cardId);
}
