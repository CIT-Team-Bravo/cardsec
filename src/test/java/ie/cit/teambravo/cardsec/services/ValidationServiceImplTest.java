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
import ie.cit.teambravo.cardsec.dto.Event;
import ie.cit.teambravo.cardsec.dto.Location;

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
		Event eventToBeSaved = new Event();
		eventToBeSaved.setPanelId(panelId);
		eventToBeSaved.setCardId(cardId);
		when(eventServiceMock.saveEvent(eventToBeSaved)).thenReturn(new Event());
		when(panelLocatorServiceMock.getPanelLocation(panelId)).thenReturn(new Location());

		// Act
		Boolean result = validationService.validate(panelId, cardId, true);

		// Assert & Verify
		assertThat(result, Matchers.is(true));
		verify(eventServiceMock).saveEvent(any(eventToBeSaved.getClass()));
	}

	@Test
	public void validate_when_requestIsNotValid_then_respondWithTrue() {
		// Arrange
		String panelId = UUID.randomUUID().toString();
		String cardId = UUID.randomUUID().toString();
		Event eventToBeSaved = new Event();
		eventToBeSaved.setPanelId(panelId);
		eventToBeSaved.setCardId(cardId);
		when(eventServiceMock.saveEvent(eventToBeSaved)).thenReturn(new Event());

		// Act
		Boolean result = validationService.validate(panelId, cardId, false);

		// Assert & Verify
		assertThat(result, Matchers.is(false));
		verify(eventServiceMock).saveEvent(any(eventToBeSaved.getClass()));
	}

}
