package ie.cit.teambravo.mqttclient;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MQTT client for receiving validation responses
 */
public class MessageClient implements MqttCallback {

	private static Logger LOGGER = LoggerFactory.getLogger(MessageClient.class);
	private MqttClient mqttClient;

	public void connect(String broker, String topic) {
		try {
			LOGGER.info("Connecting to broker {}, topic {}", broker, topic);
			mqttClient = new MqttClient(broker, "Client");
			mqttClient.setCallback(this);
			mqttClient.connect();
			mqttClient.subscribe(topic);

			while (true) {
				Thread.sleep(10000);
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			System.exit(1);
		} finally {
			try {
				LOGGER.info("Disconnecting from broker {}", broker);
				mqttClient.disconnect();
			} catch (MqttException e) {
				LOGGER.error(e.getMessage());
				System.exit(1);
			}
		}
	}

	@Override
	public void connectionLost(Throwable cause) {
		LOGGER.error("Lost connection to the broker, exiting. {}", cause.getMessage());
		cause.printStackTrace();
		System.exit(1);
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		LOGGER.info(new String(message.getPayload()));
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// Noop
	}

	public static void main(String[] args) throws Exception {

		if (args.length != 2) {
			System.out.println("Usage : <broker> <topic>");
			System.out.println("      e.g tcp://test.mosquitto.org validation.alerts");
			System.exit(1);
		}

		MessageClient client = new MessageClient();
		client.connect(args[0], args[1]);
	}
}
