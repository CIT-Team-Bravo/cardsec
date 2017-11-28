package ie.cit.teambravo.cardsec.events;

import java.util.List;

public interface EventService {
	Event saveEvent(Event eventDto);

    List<Event> findByCardId(String cardId);
    Event findLatestEventByCard(String cardId);
}
