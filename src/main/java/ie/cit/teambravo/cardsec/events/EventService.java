package ie.cit.teambravo.cardsec.events;

import java.util.List;

/**
 * API for the events package, deals with saving and finding events
 */
public interface EventService {
	Event saveEvent(Event eventDto);

	List<Event> findByCardId(String cardId);

	Event findLatestEventByCard(String cardId);
}
