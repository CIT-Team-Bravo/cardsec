package ie.cit.teambravo.cardsec.alerts;

import org.springframework.messaging.support.GenericMessage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AlertMessage extends GenericMessage<String> {

	private static final long serialVersionUID = 849222449981975916L;
	private static final ObjectMapper mapper = new ObjectMapper();

	public AlertMessage(Alert alert) throws JsonProcessingException {
		super(mapper.writeValueAsString(alert));
	}
}
