package ie.cit.teambravo.cardsec.services;

import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import ie.cit.teambravo.cardsec.alerts.AlertService;
import ie.cit.teambravo.cardsec.dto.Coordinates;
import ie.cit.teambravo.cardsec.dto.Event;
import ie.cit.teambravo.cardsec.dto.Location;
import ie.cit.teambravo.cardsec.model.LatLngAlt;

@RunWith(MockitoJUnitRunner.class)
public class ValidationServiceImplTest {
	private EventService eventServiceMock;
	private PanelLocatorService panelLocatorServiceMock;
	private AlertService alertServiceMock;
	private DurationService durationServiceMock;
	private ValidationService validationService;

	@Before
	public void setup() {
		eventServiceMock = Mockito.mock(EventService.class);
		panelLocatorServiceMock = Mockito.mock(PanelLocatorService.class);
		alertServiceMock = Mockito.mock(AlertService.class);
		durationServiceMock = Mockito.mock(DurationService.class);

		validationService = new ValidationServiceImpl(eventServiceMock, panelLocatorServiceMock, alertServiceMock,
				durationServiceMock);
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
		when(panelLocatorServiceMock.getPanelLocation(panelId))
				.thenReturn(getLocationWithCoordinates(-0.131913, 51.524774));

		mockDistanceToUclFrom(-8.4980692, 51.8960528); // cork city, fitzgerald's park

		// Act
		Boolean result = validationService.validate(panelId, cardId, true);

		// Assert & Verify
		assertThat(result, Matchers.is(true));
		verify(eventServiceMock).saveEvent(any(eventToBeSaved.getClass()));
		verify(durationServiceMock).getTravelTimeBetween2Points(any(LatLngAlt.class), any(LatLngAlt.class));
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
		when(panelLocatorServiceMock.getPanelLocation(panelId))
				.thenReturn(getLocationWithCoordinates(-0.131913, 51.524774));

		mockDistanceToUclFrom(52.237049, 21.017532); // Warsaw, Poland

		// Act
		Boolean result = validationService.validate(panelId, cardId, false);

		// Assert & Verify
		assertThat(result, Matchers.is(false));
		verify(eventServiceMock).saveEvent(any(eventToBeSaved.getClass()));
	}

	private void mockDistanceToUclFrom(Double startLat, Double startLong) {
		when(durationServiceMock.getTravelTimeBetween2Points(any(LatLngAlt.class), any(LatLngAlt.class)))
				.thenReturn(10L);
	}

	private Event getEvent(String panelId, String cardId, boolean accessAllowed, Double latitude, Double longitude) {
		Event eventDto = new Event();
		eventDto.setPanelId(panelId);
		eventDto.setCardId(cardId);
		eventDto.setAccessAllowed(accessAllowed);
		eventDto.setTimestamp(10);
		eventDto.setLocation(getLocationWithCoordinates(latitude, longitude));
		return eventDto;
	}

	private Location getLocationWithCoordinates(Double latitude, Double longitude) {
		Location location = new Location();
		Coordinates coordinatesDto = new Coordinates();
		coordinatesDto.setLatitude(latitude);
		coordinatesDto.setLongitude(longitude);
		location.setAltitude(0);
		location.setCoordinates(coordinatesDto);
		return location;
	}
}
