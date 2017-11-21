package ie.cit.teambravo.cardsec.services;

import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import ie.cit.teambravo.cardsec.dto.EventDto;
import ie.cit.teambravo.cardsec.dto.LocationDto;
import ie.cit.teambravo.cardsec.messaging.EventGateway;

@RunWith(MockitoJUnitRunner.class)
public class ValidationServiceImplTest {
	private EventService eventServiceMock;
	private PanelLocatorService panelLocatorServiceMock;
	private EventGateway eventGatewayMock;

	private ValidationService validationService;

	@Before
	public void setup() {
		eventServiceMock = Mockito.mock(EventService.class);
		panelLocatorServiceMock = Mockito.mock(PanelLocatorService.class);
		eventGatewayMock = Mockito.mock(EventGateway.class);

		validationService = new ValidationServiceImpl(eventServiceMock, panelLocatorServiceMock, eventGatewayMock);
	}

	@Test
	public void validate_when_requestIsValid_then_respondWithTrue() {
		// Arrange
		String panelId = UUID.randomUUID().toString();
		String cardId = UUID.randomUUID().toString();
		EventDto eventToBeSaved = new EventDto();
		eventToBeSaved.setPanelId(panelId);
		eventToBeSaved.setCardId(cardId);
		when(eventServiceMock.saveEvent(eventToBeSaved)).thenReturn(new EventDto());
		when(panelLocatorServiceMock.getPanelLocation(panelId)).thenReturn(new LocationDto());

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
		EventDto eventToBeSaved = new EventDto();
		eventToBeSaved.setPanelId(panelId);
		eventToBeSaved.setCardId(cardId);
		when(eventServiceMock.saveEvent(eventToBeSaved)).thenReturn(new EventDto());

		// Act
		Boolean result = validationService.validate(panelId, cardId, false);

		// Assert & Verify
		assertThat(result, Matchers.is(false));
		verify(eventServiceMock).saveEvent(any(eventToBeSaved.getClass()));
	}

	@Test
	public void validate_when_requestIsNotValid_then_publishAMessage() {
		// Arrange
		String panelId = UUID.randomUUID().toString();
		String cardId = UUID.randomUUID().toString();

		// Act
		validationService.validate(panelId, cardId, false);

		verify(eventGatewayMock, times(1)).sendToMqtt(any());
	}

	@Test
	public void validate_when_requestIsValid_then_dontPublishAMessage() {
		// Arrange
		String panelId = UUID.randomUUID().toString();
		String cardId = UUID.randomUUID().toString();

		// Act
		validationService.validate(panelId, cardId, true);

		verify(eventGatewayMock, never()).sendToMqtt(any());
	}

}
