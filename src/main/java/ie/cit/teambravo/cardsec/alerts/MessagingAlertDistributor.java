package ie.cit.teambravo.cardsec.alerts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import ie.cit.teambravo.cardsec.dto.Event;

@Service
public class MessagingAlertDistributor {

	private MessageGateway eventGateway;
	private JsonSchema schema;
	private ObjectMapper mapper;

	@Autowired
	public MessagingAlertDistributor(MessageGateway eventGateway) throws ProcessingException {
		this.eventGateway = eventGateway;
		schema = JsonSchemaFactory.byDefault().getJsonSchema("resource:/messageSchema.json");
		mapper = new ObjectMapper();
	}

	public void generateAlert(Event currentEvent, Event previousEvent) {
		try {
			Alert alert = new Alert(currentEvent, previousEvent);

			ProcessingReport result = schema.validate(mapper.valueToTree(alert));
			if (!result.isSuccess()) {
				throw new IllegalArgumentException(result.toString());
			}

			eventGateway.sendToMqtt(new AlertMessage(alert));
		} catch (Exception e) {
			throw new RuntimeException("Error publishing alert message", e);
		}
	}

}
