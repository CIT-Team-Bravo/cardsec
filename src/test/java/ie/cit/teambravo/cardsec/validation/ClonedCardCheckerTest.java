package ie.cit.teambravo.cardsec.validation;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import ie.cit.teambravo.cardsec.alerts.AlertService;
import ie.cit.teambravo.cardsec.duration.DurationService;
import ie.cit.teambravo.cardsec.events.Event;
import ie.cit.teambravo.cardsec.events.EventService;
import ie.cit.teambravo.cardsec.location.Coordinates;
import ie.cit.teambravo.cardsec.location.Location;
import ie.cit.teambravo.cardsec.location.PanelLocatorService;
import ie.cit.teambravo.cardsec.test.TestUtil;

@RunWith(MockitoJUnitRunner.class)
public class ClonedCardCheckerTest {
	private static final long TWO_MINUTES_SEC = 2 * 60;
	private static final long TEN_MINUTES_SEC = 10 * 60;
	private static final long FIVE_MINUTES_MILLISEC = 5 * 60 * 1000;

	private EventService eventServiceMock;
	private PanelLocatorService panelLocatorServiceMock;
	private AlertService alertServiceMock;
	private DurationService durationServiceMock;
	private ClonedCardChecker validationServiceImpl;

	@Before
	public void setup() {
		eventServiceMock = Mockito.mock(EventService.class);
		panelLocatorServiceMock = Mockito.mock(PanelLocatorService.class);
		alertServiceMock = Mockito.mock(AlertService.class);
		durationServiceMock = Mockito.mock(DurationService.class);

		validationServiceImpl = new ClonedCardChecker(eventServiceMock, panelLocatorServiceMock, alertServiceMock,
				durationServiceMock);
	}

	@Test
	public void validate_when_noPreviousEvent_then_respondWithTrue() {
		// Arrange
		String panelId = UUID.randomUUID().toString();
		String cardId = UUID.randomUUID().toString();

		// Act
		ValidationResponse response = validationServiceImpl.validate(panelId, cardId, Boolean.TRUE);

		// Assert
		assertThat(response.getValidEvent(), is(true));
	}

	@Test
	public void validate_when_called_then_currentEventIsStored() {
		// Arrange
		String panelId = UUID.randomUUID().toString();
		String cardId = UUID.randomUUID().toString();

		// Act
		validationServiceImpl.validate(panelId, cardId, Boolean.TRUE);

		// Assert
		ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);

		verify(eventServiceMock, times(1)).saveEvent(captor.capture());
		Event savedEvent = captor.getValue();

		assertThat(savedEvent.getCardId(), is(cardId));
		assertThat(savedEvent.getPanelId(), is(panelId));
		assertThat(savedEvent.getAccessAllowed(), is(Boolean.TRUE));
	}

	@Test
	public void validate_when_travelTimeIsLessThanTimeBetweenEvents_then_respondWithTrue() {
		// Arrange
		String panelId = UUID.randomUUID().toString();
		String cardId = UUID.randomUUID().toString();
		Event previousEvent = TestUtil.generateTestEvent();
		previousEvent.setTimestamp(previousEvent.getTimestamp() - FIVE_MINUTES_MILLISEC);

		when(eventServiceMock.findLatestEventByCard(cardId)).thenReturn(previousEvent);
		when(panelLocatorServiceMock.getPanelLocation(panelId))
				.thenReturn(getLocationWithCoordinates(-0.131913, 51.524774));
		when(durationServiceMock.getTravelTimeBetween2Points(any(), any())).thenReturn(TWO_MINUTES_SEC);

		// Act
		ValidationResponse response = validationServiceImpl.validate(panelId, cardId, Boolean.TRUE);

		// Assert
		assertThat(response.getValidEvent(), is(true));
		assertThat(response.getReason(), is("Possible time-distance event."));
	}

	@Test
	public void validate_when_travelTimeIsLessThanTimeBetweenEvents_then_alertIsNotCalled() {
		// Arrange
		String panelId = UUID.randomUUID().toString();
		String cardId = UUID.randomUUID().toString();
		Event previousEvent = TestUtil.generateTestEvent();
		previousEvent.setTimestamp(previousEvent.getTimestamp() - FIVE_MINUTES_MILLISEC);

		when(eventServiceMock.findLatestEventByCard(cardId)).thenReturn(previousEvent);
		when(panelLocatorServiceMock.getPanelLocation(panelId))
				.thenReturn(getLocationWithCoordinates(-0.131913, 51.524774));
		when(durationServiceMock.getTravelTimeBetween2Points(any(), any())).thenReturn(TWO_MINUTES_SEC);

		// Act
		validationServiceImpl.validate(panelId, cardId, Boolean.TRUE);

		// Assert
		verify(alertServiceMock, never()).generateAlert(any(), any());
	}

	@Test
	public void validate_when_travelTimeIsGreaterThanTimeBetweenEvents_then_respondWithFalse() {
		// Arrange
		String panelId = UUID.randomUUID().toString();
		String cardId = UUID.randomUUID().toString();
		Event previousEvent = TestUtil.generateTestEvent();
		previousEvent.setTimestamp(System.currentTimeMillis() - FIVE_MINUTES_MILLISEC);

		when(eventServiceMock.findLatestEventByCard(cardId)).thenReturn(previousEvent);
		when(panelLocatorServiceMock.getPanelLocation(panelId))
				.thenReturn(getLocationWithCoordinates(-0.131913, 51.524774));
		when(durationServiceMock.getTravelTimeBetween2Points(any(), any())).thenReturn(TEN_MINUTES_SEC);

		// Act
		ValidationResponse response = validationServiceImpl.validate(panelId, cardId, Boolean.TRUE);

		// Assert
		assertThat(response.getValidEvent(), is(false));
		assertThat(response.getReason(), is("Impossible time-distance event."));
	}

	@Test
	public void validate_when_travelTimeIsGreaterThanTimeBetweenEvents_then_alertIsGenerated() {
		// Arrange
		String panelId = UUID.randomUUID().toString();
		String cardId = UUID.randomUUID().toString();
		Event previousEvent = TestUtil.generateTestEvent();
		previousEvent.setTimestamp(System.currentTimeMillis() - FIVE_MINUTES_MILLISEC);

		when(eventServiceMock.findLatestEventByCard(cardId)).thenReturn(previousEvent);
		when(panelLocatorServiceMock.getPanelLocation(panelId))
				.thenReturn(getLocationWithCoordinates(-0.131913, 51.524774));
		when(durationServiceMock.getTravelTimeBetween2Points(any(), any())).thenReturn(TEN_MINUTES_SEC);

		// Act
		validationServiceImpl.validate(panelId, cardId, Boolean.TRUE);

		// Assert
		verify(alertServiceMock, times(1)).generateAlert(any(), any());
	}

	@Test
	public void validate_when_travelTimeIsGreaterThanTimeBetweenEvents_then_responseValidEventIsFALSE() {
		// Arrange
		String panelId = UUID.randomUUID().toString();
		String cardId = UUID.randomUUID().toString();
		Event previousEvent = TestUtil.generateTestEvent();
		previousEvent.setTimestamp(System.currentTimeMillis() - FIVE_MINUTES_MILLISEC);

		when(eventServiceMock.findLatestEventByCard(cardId)).thenReturn(previousEvent);
		when(panelLocatorServiceMock.getPanelLocation(panelId))
				.thenReturn(getLocationWithCoordinates(-0.131913, 51.524774));
		when(durationServiceMock.getTravelTimeBetween2Points(any(), any())).thenReturn(TEN_MINUTES_SEC);

		// Act
		ValidationResponse response = validationServiceImpl.validate(panelId, cardId, Boolean.TRUE);

		// Assert
		assertThat(response.getValidEvent(), is(Boolean.FALSE));
	}

	@Test
	public void validate_when_eventIsAllowedButTravelIsNotPossible_then_respondWithFalse() {
		// Arrange
		String panelId = UUID.randomUUID().toString();
		String cardId = UUID.randomUUID().toString();
		Event previousEvent = TestUtil.generateTestEvent();
		previousEvent.setTimestamp(System.currentTimeMillis() - FIVE_MINUTES_MILLISEC);

		when(eventServiceMock.findLatestEventByCard(cardId)).thenReturn(previousEvent);
		when(panelLocatorServiceMock.getPanelLocation(panelId))
				.thenReturn(getLocationWithCoordinates(-0.131913, 51.524774));
		when(durationServiceMock.getTravelTimeBetween2Points(any(), any())).thenReturn(TEN_MINUTES_SEC);

		// Act
		ValidationResponse response = validationServiceImpl.validate(panelId, cardId, Boolean.TRUE);

		// Assert
		assertThat(response.getValidEvent(), is(false));
	}

	@Test
	public void validate_when_panelIdIsInvalid_then_exception_isThrown() {
		// Arrange
		String panelId = UUID.randomUUID().toString();
		String cardId = UUID.randomUUID().toString();
		when(panelLocatorServiceMock.getPanelLocation(panelId)).thenThrow(new RuntimeException("Test exception"));

		// Act / Assert
		try {
			validationServiceImpl.validate(panelId, cardId, Boolean.TRUE);
			fail("Expecting IllegalArgumentException here");
		} catch (Exception e) {
			assert (e instanceof IllegalArgumentException);
			assertThat(e.getCause().getMessage(), is("Test exception"));
		}

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
