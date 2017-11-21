package ie.cit.teambravo.cardsec.alerts;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import ie.cit.teambravo.cardsec.dto.EventDto;

public class MessagingAlertDistributorTest {

	private MessageGateway mockEventGateway;
	private MessagingAlertDistributor messagingAlertDistributor;
	private JsonSchema schema;
	private ObjectMapper mapper;

	@Before
	public void setup() throws ProcessingException {
		mockEventGateway = Mockito.mock(MessageGateway.class);
		messagingAlertDistributor = new MessagingAlertDistributor(mockEventGateway);
		schema = JsonSchemaFactory.byDefault().getJsonSchema("resource:/messageSchema.json");
		mapper = new ObjectMapper();
	}

	@Test
	public void generateAlert_when_called_then_theMessageIsFormattedCorrectly() throws Exception {
		// Arrange
		EventDto currentEvent = new EventDto();
		EventDto previousEvent = new EventDto();

		ArgumentCaptor<AlertMessage> captor = ArgumentCaptor.forClass(AlertMessage.class);

		// Act
		messagingAlertDistributor.generateAlert(currentEvent, previousEvent);

		// Assert
		verify(mockEventGateway, times(1)).sendToMqtt(captor.capture());
		String jsonPayload = captor.getValue().getPayload();

		ProcessingReport result = schema.validate(mapper.readTree(jsonPayload), true);
		assertTrue(result.isSuccess());
	}
}
