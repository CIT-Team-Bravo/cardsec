package ie.cit.teambravo.cardsec.alerts;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.Message;

@MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
public interface MessageGateway {
	void sendToMqtt(Message<String> message);
}
