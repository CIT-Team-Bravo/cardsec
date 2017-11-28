package ie.cit.teambravo.cardsec.services;

import ie.cit.teambravo.cardsec.events.Event;
import ie.cit.teambravo.cardsec.events.EventRepository;
import ie.cit.teambravo.cardsec.events.EventServiceImpl;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EventServiceImplTest {
    @Mock
    private EventRepository eventRepositoryMock;

    @InjectMocks
    private EventServiceImpl eventService;

    @Test
    public void saveEvent_when_eventIsSupplied_then_eventIsSaved() {
        // Arrange
        Event eventDtoToBeSaved = getEvent("panelId", "cardId", true);
        when(eventRepositoryMock.save(eventDtoToBeSaved)).thenReturn(getEvent("panelId", "cardId", true));

        // Act
        Event result = eventService.saveEvent(eventDtoToBeSaved);

        // Assert & verify
        assertThat(result, CoreMatchers.notNullValue());
        assertThat(result.getPanelId(), is(eventDtoToBeSaved.getPanelId()));
        assertThat(result.getCardId(), is(eventDtoToBeSaved.getCardId()));
        assertThat(result.getAccessAllowed(), is(Boolean.TRUE));
        verify(eventRepositoryMock).save(eventDtoToBeSaved);
    }

    @Test
    public void findByCardId_when_matchingEventsFound_then_eventListIsReturned() {
        // Arrange
        Event event1 = getEvent("panelId", "cardId", true);
        Event event2 = getEvent("panelId", "cardId", true);
        when(eventRepositoryMock.findByCardId(event1.getCardId())).thenReturn(Arrays.asList(event1, event2));

        // Act
        List<Event> results = eventService.findByCardId(event2.getCardId());

        // Assert & Verify
        assertThat(results, notNullValue());
        assertThat(results, hasSize(2));
        results.forEach(result -> {
            assertThat(result.getPanelId(), is(event1.getPanelId()));
            assertThat(result.getCardId(), is(event1.getCardId()));
            assertThat(result.getAccessAllowed(), is(event1.getAccessAllowed()));
        });
        verify(eventRepositoryMock).findByCardId(event1.getCardId());
    }

    @Test
    public void findLatestEventByCard_when_called_then_latestEventIsReturned(){
        // Arrange
        String cardIdToBeFound = "cardId";
        Event event = getEvent("panelId2", cardIdToBeFound, true);
        when(eventRepositoryMock.findFirstByCardIdOrderByTimestampDesc(cardIdToBeFound)).thenReturn(event);

        // Act
        Event result = eventService.findLatestEventByCard(cardIdToBeFound);

        // Assert & Verify
        assertThat(result, notNullValue());
        assertThat(result.getPanelId(), is(event.getPanelId()));
        assertThat(result.getCardId(), is(event.getCardId()));
        assertThat(result.getAccessAllowed(), is(event.getAccessAllowed()));

        verify(eventRepositoryMock).findFirstByCardIdOrderByTimestampDesc(cardIdToBeFound);
    }

    private Event getEvent(String panelId, String cardId, boolean accessAllowed) {
        Event event = new Event();
        event.setPanelId(panelId);
        event.setCardId(cardId);
        event.setAccessAllowed(accessAllowed);
        event.setTimestamp(10L);
        return event;
    }
}