package ie.cit.teambravo.cardsec.messaging;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.Message;

@MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
public interface EventGateway {
	void sendToMqtt(Message<String> message);
}
