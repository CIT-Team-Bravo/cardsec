package ie.cit.teambravo.cardsec.alerts;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import ie.cit.teambravo.cardsec.dto.EventDto;

public class MessagingAlertDistributorTest {

	private MessageGateway mockEventGateway;
	private MessagingAlertDistributor messagingAlertDistributor;

	@Before
	public void setup() {
		mockEventGateway = Mockito.mock(MessageGateway.class);

		messagingAlertDistributor = new MessagingAlertDistributor(mockEventGateway);
	}

	@Test
	public void generateAlert_when_called_then_theMessageIsFormattedCorrectly() {
		// Arrange
		EventDto currentEvent = new EventDto();
		EventDto previousEvent = new EventDto();

		ArgumentCaptor<AlertMessage> captor = ArgumentCaptor.forClass(AlertMessage.class);

		// Act
		messagingAlertDistributor.generateAlert(currentEvent, previousEvent);

		// Assert
		verify(mockEventGateway, times(1)).sendToMqtt(captor.capture());
	}
}
