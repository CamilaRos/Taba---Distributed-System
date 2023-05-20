import org.eclipse.paho.client.mqttv3.*;
import java.util.Date;

// Class Definition
public class Subscriber_Floor {

    // Main Method
    public static void main(String[] args) throws MqttException {
        MqttClient client = new MqttClient("tcp://localhost:1883", MqttClient.generateClientId());
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setWill("Floor and Room", "Client Disconnected".getBytes(), 2, true);
        client.connect(mqttConnectOptions); // Connect to MQTT broker
        client.setCallback(new SubCallback());

        // Subscribe to specific MQTT topics
        client.subscribe("floor/+/id"); // Subscribe to topics matching "floor/+/id" pattern
        client.subscribe("floor/+/location"); // Subscribe to topics matching "floor/+/location" pattern
        client.subscribe("/floor/Lost"); // Subscribe to the exact topic "/floor/Lost"
    }

    // Implementation for handling MQTT events
    private static class SubCallback implements MqttCallback {
        public void connectionLost(Throwable throwable) {
            System.out.println("Connection lost");
        }

        // When the message arrives
        public void messageArrived(String topic, MqttMessage mqttMessage) {
            System.out.println("----" + new Date().toGMTString() + "---- Message received! ✓");
            System.out.println("Message content: " + new String(mqttMessage.getPayload()));
        }

        // When the message delivery is complete
        public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        }
    }
}
