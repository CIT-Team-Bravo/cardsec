package ie.cit.teambravo.cardsec.validation;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import ie.cit.teambravo.cardsec.events.Event;
import ie.cit.teambravo.cardsec.test.TestUtil;

public class ValidationControllerTest {

	private JsonSchema schema;
	private ValidationService validationServiceMock;

	private ValidationController validationController;
	private ObjectMapper mapper;

	@Before
	public void setup() throws ProcessingException {
		validationServiceMock = Mockito.mock(ValidationService.class);
		validationController = new ValidationController(validationServiceMock);

		schema = JsonSchemaFactory.byDefault().getJsonSchema("resource:/restResponseSchema.json");
		mapper = new ObjectMapper();
	}

	@Test
	public void validationRequest_when_called_then_a_correctlyFormattedResponseIsReturned() throws Exception {
		Event currentEvent = TestUtil.generateTestEvent();
		Event previousEvent = TestUtil.generateTestEvent();

		ValidationResponse serviceResponse = new ValidationResponse(currentEvent, previousEvent, true);

		Mockito.when(validationServiceMock.validate(currentEvent.getPanelId(), currentEvent.getCardId(), Boolean.TRUE))
				.thenReturn(serviceResponse);

		ValidationResponse response = validationController.validationRequest(currentEvent.getPanelId(),
				currentEvent.getCardId(), true);

		ProcessingReport result = schema.validate(mapper.valueToTree(response));
		assertTrue(result.toString(), result.isSuccess());
	}
}
