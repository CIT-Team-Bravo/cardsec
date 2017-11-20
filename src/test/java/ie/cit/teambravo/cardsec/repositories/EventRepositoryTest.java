package ie.cit.teambravo.cardsec.repositories;

import ie.cit.teambravo.cardsec.dto.EventDto;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EventRepositoryTest {
    @Autowired
    private EventRepository eventRepository;

    @After
    public void teardown(){
        eventRepository.deleteAll();
    }

    @Test
    public void save_when_eventObjectSupplied_then_eventObjectIsSaved_and_returned() {
        // Arrange
        EventDto eventDto = getEventDto("panel-id");

        // Act
        EventDto result = eventRepository.save(eventDto);

        // Assert
        assertThat(result, CoreMatchers.notNullValue());
        assertThat(result.getPanelId(), is(eventDto.getPanelId()));
        assertThat(result.getCardId(), is(eventDto.getCardId()));
        assertThat(result.getAccessAllowed(), is(Boolean.TRUE));
    }


    @Test
    public void findByCardId_when_cardIdIsFound_then_listOfMatchingEventsIsReturned() {
        // Arrange
        EventDto eventDto1 = getEventDto("panel-id");
        EventDto eventDto2 = getEventDto("panel-id");

        eventRepository.save(eventDto1);
        eventRepository.save(eventDto2);

        // Act
        List<EventDto> results = eventRepository.findByCardId(eventDto1.getCardId());

        // Assert
        assertThat(results, notNullValue());
        assertThat(results, hasSize(2));
        results.forEach(result -> {
            assertThat(result.getPanelId(), is(eventDto1.getPanelId()));
            assertThat(result.getCardId(), is(eventDto1.getCardId()));
            assertThat(result.getAccessAllowed(), is(eventDto1.getAccessAllowed()));
        });

    }

    @Test
    public void  findFirstByCardIdOrderByTimestampDesc_when_cardIdIsFound_then_latestEventIsReturned() {
        // Arrange
        EventDto eventDto1 = getEventDto("panel-id");
        eventRepository.save(eventDto1);

        EventDto eventDto2 = getEventDto("panel-id2");
        eventRepository.save(eventDto2);

        // Act
        EventDto result = eventRepository.findFirstByCardIdOrderByTimestampDesc(eventDto1.getCardId());

        // Assert
        assertThat(result, notNullValue());
        assertThat(result.getPanelId(), is(eventDto2.getPanelId()));
        assertThat(result.getCardId(), is(eventDto2.getCardId()));
        assertThat(result.getAccessAllowed(), is(eventDto2.getAccessAllowed()));
    }

    @Test
    public void findByCardId_when_cardIdIsNotFound_then_emptyEventListIsReturned() {
        // Arrange
        EventDto eventDto = getEventDto("panel-id");
        eventRepository.save(eventDto);

        // Act
        List<EventDto> results = eventRepository.findByCardId("new-card-id");

        // Assert
        assertThat(results, notNullValue());
        assertThat(results, hasSize(0));

    }

    private EventDto getEventDto(String panelId) {
        EventDto eventDto = new EventDto();
        eventDto.setPanelId(panelId);
        eventDto.setCardId("card-001");
        eventDto.setAccessAllowed(true);
        eventDto.setTimestamp(new Date());
        return eventDto;
    }

}