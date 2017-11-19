package ie.cit.teambravo.cardsec.repositories;

import ie.cit.teambravo.cardsec.dto.EventDto;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EventRepository extends CrudRepository<EventDto, String> {

    List<EventDto> findByCardId(String cardId);

}