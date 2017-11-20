package ie.cit.teambravo.cardsec.services;

import ie.cit.teambravo.cardsec.dto.EventDto;
import ie.cit.teambravo.cardsec.repositories.EventRepository;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Date;
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
        EventDto eventDtoToBeSaved = getEventDto("panelId", "cardId", true);
        when(eventRepositoryMock.save(eventDtoToBeSaved)).thenReturn(getEventDto("panelId", "cardId", true));

        // Act
        EventDto result = eventService.saveEvent(eventDtoToBeSaved);

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
        EventDto eventDto1 = getEventDto("panelId", "cardId", true);
        EventDto eventDto2 = getEventDto("panelId", "cardId", true);
        when(eventRepositoryMock.findByCardId(eventDto1.getCardId())).thenReturn(Arrays.asList(eventDto1, eventDto2));

        // Act
        List<EventDto> results = eventService.findByCardId(eventDto2.getCardId());

        // Assert & Verify
        assertThat(results, notNullValue());
        assertThat(results, hasSize(2));
        results.forEach(result -> {
            assertThat(result.getPanelId(), is(eventDto1.getPanelId()));
            assertThat(result.getCardId(), is(eventDto1.getCardId()));
            assertThat(result.getAccessAllowed(), is(eventDto1.getAccessAllowed()));
        });
        verify(eventRepositoryMock).findByCardId(eventDto1.getCardId());
    }

    @Test
    public void findLatestEventByCard_when_called_then_latestEventIsReturned(){
        // Arrange
        String cardIdToBeFound = "cardId";
        EventDto eventDto1 = getEventDto("panelId1", cardIdToBeFound, false);
        EventDto eventDto2 = getEventDto("panelId2", cardIdToBeFound, true);
        when(eventRepositoryMock.findFirstByCardIdOrderByTimestampDesc(cardIdToBeFound)).thenReturn(eventDto2);

        // Act
        EventDto result = eventService.findLatestEventByCard(cardIdToBeFound);

        // Assert & Verify
        assertThat(result, notNullValue());
        assertThat(result.getPanelId(), is(eventDto2.getPanelId()));
        assertThat(result.getCardId(), is(eventDto2.getCardId()));
        assertThat(result.getAccessAllowed(), is(eventDto2.getAccessAllowed()));

        verify(eventRepositoryMock).findFirstByCardIdOrderByTimestampDesc(cardIdToBeFound);
    }

    private EventDto getEventDto(String panelId, String cardId, boolean accessAllowed) {
        EventDto eventDto = new EventDto();
        eventDto.setPanelId(panelId);
        eventDto.setCardId(cardId);
        eventDto.setAccessAllowed(accessAllowed);
        eventDto.setTimestamp(new Date());
        return eventDto;
    }
}