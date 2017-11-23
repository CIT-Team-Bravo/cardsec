package ie.cit.teambravo.cardsec.repositories;

import ie.cit.teambravo.cardsec.dto.Event;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EventRepository extends CrudRepository<Event, String> {

	List<Event> findByCardId(String cardId);

}
