package ie.cit.teambravo.cardsec.alerts;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import ie.cit.teambravo.cardsec.dto.EventDto;

@Service
public class MessagingAlertDistributor {

	private MessageGateway eventGateway;

	public MessagingAlertDistributor(MessageGateway eventGateway) {
		this.eventGateway = eventGateway;
	}

	public void generateAlert(EventDto currentEvent, EventDto previousEvent) {
		try {
			Alert alert = new Alert(currentEvent, previousEvent);
			eventGateway.sendToMqtt(new AlertMessage(alert));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
