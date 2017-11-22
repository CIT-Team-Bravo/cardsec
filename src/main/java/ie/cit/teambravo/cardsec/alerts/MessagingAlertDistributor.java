package ie.cit.teambravo.cardsec.alerts;

import com.fasterxml.jackson.core.JsonProcessingException;
import ie.cit.teambravo.cardsec.dto.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessagingAlertDistributor {

	private MessageGateway eventGateway;

	@Autowired
	public MessagingAlertDistributor(MessageGateway eventGateway) {
		this.eventGateway = eventGateway;
	}

	public void generateAlert(Event currentEvent, Event previousEvent) {
		try {
			Alert alert = new Alert(currentEvent, previousEvent);
			eventGateway.sendToMqtt(new AlertMessage(alert));
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error publishing alert message", e);
		}
	}

}
