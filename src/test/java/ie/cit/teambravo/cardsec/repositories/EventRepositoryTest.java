package ie.cit.teambravo.cardsec.repositories;

import ie.cit.teambravo.cardsec.dto.Event;
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
        Event event = getEvent("panel-id");

        // Act
        Event result = eventRepository.save(event);

        // Assert
        assertThat(result, CoreMatchers.notNullValue());
        assertThat(result.getPanelId(), is(event.getPanelId()));
        assertThat(result.getCardId(), is(event.getCardId()));
        assertThat(result.getAccessAllowed(), is(Boolean.TRUE));
    }


    @Test
    public void findByCardId_when_cardIdIsFound_then_listOfMatchingEventsIsReturned() {
        // Arrange
        Event event1 = getEvent("panel-id");
        Event event2 = getEvent("panel-id");

        eventRepository.save(event1);
        eventRepository.save(event2);

        // Act
        List<Event> results = eventRepository.findByCardId(event1.getCardId());

        // Assert
        assertThat(results, notNullValue());
        assertThat(results, hasSize(2));
        results.forEach(result -> {
            assertThat(result.getPanelId(), is(event1.getPanelId()));
            assertThat(result.getCardId(), is(event1.getCardId()));
            assertThat(result.getAccessAllowed(), is(event1.getAccessAllowed()));
        });

    }

    @Test
    public void  findFirstByCardIdOrderByTimestampDesc_when_cardIdIsFound_then_latestEventIsReturned() {
        // Arrange
        Event event1 = getEvent("panel-id");
        eventRepository.save(event1);

        Event event2 = getEvent("panel-id2");

        eventRepository.save(event2);

        // Act
        Event result = eventRepository.findFirstByCardIdOrderByTimestampDesc(event1.getCardId());

        // Assert
        assertThat(result, notNullValue());
        assertThat(result.getPanelId(), is(event2.getPanelId()));
        assertThat(result.getCardId(), is(event2.getCardId()));
        assertThat(result.getAccessAllowed(), is(event2.getAccessAllowed()));
    }

    @Test
    public void findByCardId_when_cardIdIsNotFound_then_emptyEventListIsReturned() {
        // Arrange
        Event event = getEvent("panel-id");
        eventRepository.save(event);

        // Act
        List<Event> results = eventRepository.findByCardId("new-card-id");

        // Assert
        assertThat(results, notNullValue());
        assertThat(results, hasSize(0));

    }

    private Event getEvent(String panelId) {
        Event event = new Event();
        event.setPanelId(panelId);
        event.setCardId("card-001");
        event.setAccessAllowed(true);
        event.setTimestamp(new Date().getTime());
        return event;
    }

}