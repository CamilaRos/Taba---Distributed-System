import org.eclipse.paho.client.mqttv3.*;
import java.util.Date;

// Class Definition
public class Subscriber_Room {

    // Main Method
    public static void main(String[] args) throws MqttException {
        MqttClient client = new MqttClient("tcp://localhost:1883", MqttClient.generateClientId());
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setWill("floor/room/will", "Client Disconnected".getBytes(), 2, true);
        client.connect(mqttConnectOptions);
        client.setCallback(new SubCallback());
        client.subscribe("floor/room/#");
    }

    // Implementation for handling MQTT events
    private static class SubCallback implements MqttCallback {
        public void connectionLost(Throwable throwable) {
            System.out.println("Connection lost");
        }

        // When the message arrives
        public void messageArrived(String topic, MqttMessage mqttMessage) {
            System.out.println("n---- " + new Date().toGMTString() + " ---- Message received! ✓ " );
            System.out.println("Message content: " + new String(mqttMessage.getPayload()));
        }

        // When the message delivery is complete
        public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        }
    }
}
