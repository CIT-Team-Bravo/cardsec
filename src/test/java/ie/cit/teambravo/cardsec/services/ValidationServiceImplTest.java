package ie.cit.teambravo.cardsec.services;

import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixRow;
import ie.cit.teambravo.cardsec.dto.CoordinatesDto;
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
        EventDto eventToBeSaved = getEventDto(panelId, cardId, true, -0.131913, 51.524774);
        eventToBeSaved.setPanelId(panelId);
        eventToBeSaved.setCardId(cardId);
        EventDto eventDto2 = getEventDto(panelId, cardId, true, -8.4980692, 51.8960528);

        when(eventServiceMock.findLatestEventByCard(cardId)).thenReturn(eventDto2);
        when(eventServiceMock.saveEvent(eventToBeSaved)).thenReturn(new EventDto());
        when(panelLocatorServiceMock.getPanelLocation(panelId)).thenReturn(getLocationDtoWithCoordinates(-0.131913, 51.524774));

        mockDistanceToUclFrom(-8.4980692, 51.8960528); //cork city, fitzgerald's park

        // Act
        Boolean result = validationService.validate(panelId, cardId, true);

        // Assert & Verify
        assertThat(result, Matchers.is(true));
        verify(eventServiceMock).saveEvent(any(eventToBeSaved.getClass()));
        verify(distanceServiceMock).getDistanceBetween2Points(-8.4980692, 51.8960528, -0.131913, 51.524774);
    }

    @Test
    public void validate_when_requestIsNotValid_then_respondWithFalse() {
        // Arrange
        String panelId = UUID.randomUUID().toString();
        String cardId = UUID.randomUUID().toString();
        EventDto eventToBeSaved = new EventDto();
        eventToBeSaved.setPanelId(panelId);
        eventToBeSaved.setCardId(cardId);
        EventDto eventDto1 = getEventDto("panelId", "cardId", true, 0.0D, 0.0D);
        EventDto eventDto2 = getEventDto("panelId", "cardId", true, 0.0D, 0.0D);
        when(eventServiceMock.findLatestEventByCard(cardId)).thenReturn(eventDto2);
        when(eventServiceMock.saveEvent(eventToBeSaved)).thenReturn(new EventDto());
        when(panelLocatorServiceMock.getPanelLocation(panelId)).thenReturn(getLocationDtoWithCoordinates(-0.131913, 51.524774));

        mockDistanceToUclFrom(52.237049, 21.017532); // Warsaw, Poland

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

    private EventDto getEventDto(String panelId, String cardId, boolean accessAllowed, Double latitude, Double longitude) {
        EventDto eventDto = new EventDto();
        eventDto.setPanelId(panelId);
        eventDto.setCardId(cardId);
        eventDto.setAccessAllowed(accessAllowed);
        eventDto.setTimestamp(new Date());
        eventDto.setLocationDto(getLocationDtoWithCoordinates(latitude, longitude));
        return eventDto;
    }

    private LocationDto getLocationDtoWithCoordinates(Double latitude, Double longitude) {
        LocationDto locationDto = new LocationDto();
        CoordinatesDto coordinatesDto = new CoordinatesDto();
        coordinatesDto.setLatitude(latitude);
        coordinatesDto.setLongitude(longitude);
        locationDto.setCoordinates(coordinatesDto);
        return locationDto;
    }
}