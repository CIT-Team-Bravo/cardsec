package ie.cit.teambravo.cardsec.events;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventServiceImpl implements EventService {

	@Autowired
	private EventRepository eventRepository;

	@Override
	public Event saveEvent(Event eventDto) {
		return eventRepository.save(eventDto);
	}

	@Override
	public List<Event> findByCardId(String cardId) {
		return eventRepository.findByCardId(cardId);
	}

	@Override
	public Event findLatestEventByCard(String cardId) {
		return eventRepository.findFirstByCardIdOrderByTimestampDesc(cardId);
	}
}