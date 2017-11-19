package ie.cit.teambravo.cardsec.services;

import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixRow;
import ie.cit.teambravo.cardsec.dto.EventDto;
import ie.cit.teambravo.cardsec.dto.LocationDto;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ValidationServiceImplTest {
    @Mock
    private EventService eventServiceMock;
    @Mock
    private PanelLocatorService panelLocatorServiceMock;
    @Mock
    private DistanceService distanceServiceMock;
    @InjectMocks
    private ValidationService validationService = new ValidationServiceImpl();

    @Test
    public void validate_when_requestIsValid_then_respondWithTrue() {
        // Arrange
        String panelId = UUID.randomUUID().toString();
        String cardId = UUID.randomUUID().toString();
        EventDto eventToBeSaved = new EventDto();
        eventToBeSaved.setPanelId(panelId);
        eventToBeSaved.setCardId(cardId);
        EventDto eventDto1 = getEventDto("panelId", "cardId", true);
        EventDto eventDto2 = getEventDto("panelId", "cardId", true);
        when(eventServiceMock.findByCardId(eventDto1.getCardId())).thenReturn(Arrays.asList(eventDto1, eventDto2));
        when(eventServiceMock.saveEvent(eventToBeSaved)).thenReturn(new EventDto());
        when(panelLocatorServiceMock.getPanelLocation(panelId)).thenReturn(new LocationDto());
        mockDistanceToUclFrom(-8.4980692,51.8960528);

        // Act
        Boolean result = validationService.validate(panelId, cardId, true);

        // Assert & Verify
        assertThat(result, Matchers.is(true));
        verify(eventServiceMock).saveEvent(any(eventToBeSaved.getClass()));
        verify(distanceServiceMock).getDistanceBetween2Points(-8.4980692,51.8960528, -0.131913, 51.524774);
    }

    @Test
    public void validate_when_requestIsNotValid_then_respondWithTrue() {
        // Arrange
        String panelId = UUID.randomUUID().toString();
        String cardId = UUID.randomUUID().toString();
        EventDto eventToBeSaved = new EventDto();
        eventToBeSaved.setPanelId(panelId);
        eventToBeSaved.setCardId(cardId);
        EventDto eventDto1 = getEventDto("panelId", "cardId", true);
        EventDto eventDto2 = getEventDto("panelId", "cardId", true);
        when(eventServiceMock.findByCardId(eventDto1.getCardId())).thenReturn(Arrays.asList(eventDto1, eventDto2));
        when(eventServiceMock.saveEvent(eventToBeSaved)).thenReturn(new EventDto());

        // Act
        Boolean result = validationService.validate(panelId, cardId, false);

        // Assert & Verify
        assertThat(result, Matchers.is(false));
        verify(eventServiceMock).saveEvent(any(eventToBeSaved.getClass()));
    }

    private void mockDistanceToUclFrom(Double startLat, Double startLong) {
        DistanceMatrixRow distanceMatrixRow = new DistanceMatrixRow();
        DistanceMatrixRow[] distanceMatrixRows = new DistanceMatrixRow[]{distanceMatrixRow};
        when(distanceServiceMock.getDistanceBetween2Points(startLat, startLong, -0.131913, 51.524774)).thenReturn(new DistanceMatrix(new String[]{"start"}, new String[]{"finish"}, distanceMatrixRows));

    }

    private EventDto getEventDto(String panelId, String cardId, boolean accessAllowed) {
        EventDto eventDto = new EventDto();
        eventDto.setPanelId(panelId);
        eventDto.setCardId(cardId);
        eventDto.setAccessAllowed(accessAllowed);
        eventDto.setTimestamp(new Date().toString());
        return eventDto;
    }
}