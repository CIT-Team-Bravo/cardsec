package ie.cit.teambravo.cardsec.services;

import ie.cit.teambravo.cardsec.dto.Event;
import ie.cit.teambravo.cardsec.repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
