package ie.cit.teambravo.cardsec.services;

import ie.cit.teambravo.cardsec.dto.Event;

import java.util.List;

public interface EventService {
    Event saveEvent(Event eventDto);

    List<Event> findByCardId(String cardId);
    Event findLatestEventByCard(String cardId);
}
