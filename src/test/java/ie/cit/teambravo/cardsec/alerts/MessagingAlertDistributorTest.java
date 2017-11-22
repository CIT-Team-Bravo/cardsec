package ie.cit.teambravo.cardsec.alerts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import ie.cit.teambravo.cardsec.dto.Coordinates;
import ie.cit.teambravo.cardsec.dto.Event;
import ie.cit.teambravo.cardsec.dto.Location;

public class MessagingAlertDistributorTest {

	private MessageGateway mockEventGateway;
	private MessagingAlertDistributor messagingAlertDistributor;
	private JsonSchema schema;
	private ObjectMapper mapper;

	@Before
	public void setup() throws ProcessingException {
		mockEventGateway = Mockito.mock(MessageGateway.class);
		schema = JsonSchemaFactory.byDefault().getJsonSchema("resource:/messageSchema.json");
		mapper = new ObjectMapper();
		messagingAlertDistributor = new MessagingAlertDistributor(mockEventGateway);

	}

	@Test
	public void generateAlert_when_called_then_theMessageIsFormattedCorrectly() throws Exception {
		// Arrange
		Event currentEvent = generateTestEvent();
		Event previousEvent = generateTestEvent();

		ArgumentCaptor<AlertMessage> captor = ArgumentCaptor.forClass(AlertMessage.class);

		// Act
		messagingAlertDistributor.generateAlert(currentEvent, previousEvent);

		// Assert
		verify(mockEventGateway, times(1)).sendToMqtt(captor.capture());
		String jsonPayload = captor.getValue().getPayload();

		ProcessingReport result = schema.validate(mapper.readTree(jsonPayload), true);
		assertTrue(result.toString(), result.isSuccess());
	}

	@Test
	public void generateAlert_when_calledWithBadArguments_then_throwsException() throws Exception {
		// Arrange
		Event currentEvent = generateTestEvent();

		// Act
		try {
			messagingAlertDistributor.generateAlert(currentEvent, null);
			fail("Expected an exception here, because the previousEvent was null");
		} catch (Exception e) {
			// Assert
			assertEquals("Error publishing alert message", e.getMessage());
			assertEquals(IllegalArgumentException.class, e.getCause().getClass());
		}

	}

	@Test
	public void assignmentExampleJson_when_checkedAgainstSchema_then_isValid() throws Exception {
		File testFile = new File(
				getClass().getClassLoader().getResource("exampleMessageFromAssignment.json").getPath());

		String exampleMessageJson = FileUtils.readFileToString(testFile);

		ProcessingReport result = schema.validate(mapper.readTree(exampleMessageJson), true);
		assertTrue(result.toString(), result.isSuccess());
	}

	private Event generateTestEvent() {
		Event anEventWhereTheDataIsNotImportant = new Event();
		anEventWhereTheDataIsNotImportant.setPanelId(UUID.randomUUID().toString());
		anEventWhereTheDataIsNotImportant.setCardId(UUID.randomUUID().toString());
		anEventWhereTheDataIsNotImportant.setTimestamp(System.currentTimeMillis());
		anEventWhereTheDataIsNotImportant.setAccessAllowed(false);

		Location location = new Location();
		location.setAltitude(100);
		location.setRelativeLocation("Someplace, TX, USA");

		Coordinates coordinates = new Coordinates();
		coordinates.setLatitude(10.1);
		coordinates.setLongitude(1.10);

		location.setCoordinates(coordinates);

		anEventWhereTheDataIsNotImportant.setLocation(location);

		return anEventWhereTheDataIsNotImportant;
	}
}
