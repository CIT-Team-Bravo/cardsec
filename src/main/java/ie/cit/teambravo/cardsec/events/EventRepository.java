package ie.cit.teambravo.cardsec.events;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EventRepository extends CrudRepository<Event, String> {

	List<Event> findByCardId(String cardId);

    Event findFirstByCardIdOrderByTimestampDesc(String cardId);

}