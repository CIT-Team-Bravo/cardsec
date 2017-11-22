package ie.cit.teambravo.cardsec.alerts;

import java.io.IOException;

import org.springframework.messaging.support.GenericMessage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;

public class AlertMessage extends GenericMessage<String> {

	private static final long serialVersionUID = 849222449981975916L;
	private static final ObjectMapper mapper = new ObjectMapper();

	public AlertMessage(Alert alert) throws ProcessingException, IOException {
		super(mapper.writeValueAsString(alert));
	}
}
