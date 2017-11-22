package ie.cit.teambravo.cardsec.services;

import com.google.maps.model.DistanceMatrixRow;
import ie.cit.teambravo.cardsec.dto.Coordinates;
import ie.cit.teambravo.cardsec.dto.Event;
import ie.cit.teambravo.cardsec.dto.Location;
import ie.cit.teambravo.cardsec.model.LatLngAlt;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.UUID;

import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ValidationServiceImplTest {

	@Mock
	private EventService eventServiceMock;
	@Mock
	private PanelLocatorService panelLocatorServiceMock;
	@Mock
	private DurationService durationServiceMock;
	@InjectMocks
	private ValidationService validationService = new ValidationServiceImpl(eventServiceMock, panelLocatorServiceMock,
			durationServiceMock);

	@Before
	public void setup() {
		when(durationServiceMock.getTravelTimeBetween2Points(anyObject(), anyObject())).thenReturn(1L);
	}

    @Test
    public void validate_when_requestIsValid_then_respondWithTrue() {
        // Arrange
        String panelId = UUID.randomUUID().toString();
        String cardId = UUID.randomUUID().toString();
        Event eventToBeSaved = getEvent(panelId, cardId, true, -0.131913, 51.524774);
        eventToBeSaved.setPanelId(panelId);
        eventToBeSaved.setCardId(cardId);
        Event eventDto2 = getEvent(panelId, cardId, true, -8.4980692, 51.8960528);

        when(eventServiceMock.findLatestEventByCard(cardId)).thenReturn(eventDto2);
        when(eventServiceMock.saveEvent(eventToBeSaved)).thenReturn(new Event());
        when(panelLocatorServiceMock.getPanelLocation(panelId)).thenReturn(getLocationDtoWithCoordinates(-0.131913, 51.524774));

        mockDistanceToUclFrom(-8.4980692, 51.8960528); //cork city, fitzgerald's park

        // Act
        Boolean result = validationService.validate(panelId, cardId, true);

        // Assert & Verify
        assertThat(result, Matchers.is(true));
        verify(eventServiceMock).saveEvent(any(eventToBeSaved.getClass()));
        verify(durationServiceMock).getTravelTimeBetween2Points(new LatLngAlt(0,0,0), new LatLngAlt(0,0,0));
    }

    @Test
    public void validate_when_requestIsNotValid_then_respondWithFalse() {
        // Arrange
        String panelId = UUID.randomUUID().toString();
        String cardId = UUID.randomUUID().toString();
        Event eventToBeSaved = new Event();
        eventToBeSaved.setPanelId(panelId);
        eventToBeSaved.setCardId(cardId);
        Event eventDto1 = getEvent("panelId", "cardId", true, 0.0D, 0.0D);
        Event eventDto2 = getEvent("panelId", "cardId", true, 0.0D, 0.0D);
        when(eventServiceMock.findLatestEventByCard(cardId)).thenReturn(eventDto2);
        when(eventServiceMock.saveEvent(eventToBeSaved)).thenReturn(new Event());
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
        when(durationServiceMock.getTravelTimeBetween2Points(new LatLngAlt(0,0,0), new LatLngAlt(0,0,0))).thenReturn(10L);
    }

    private Event getEvent(String panelId, String cardId, boolean accessAllowed, Double latitude, Double longitude) {
        Event eventDto = new Event();
        eventDto.setPanelId(panelId);
        eventDto.setCardId(cardId);
        eventDto.setAccessAllowed(accessAllowed);
        eventDto.setTimestamp(10);
        eventDto.setLocation(getLocationDtoWithCoordinates(latitude, longitude));
        return eventDto;
    }

    private Location getLocationDtoWithCoordinates(Double latitude, Double longitude) {
        Location locationDto = new Location();
        Coordinates coordinatesDto = new Coordinates();
        coordinatesDto.setLatitude(latitude);
        coordinatesDto.setLongitude(longitude);
        locationDto.setCoordinates(coordinatesDto);
        return locationDto;
    }
}