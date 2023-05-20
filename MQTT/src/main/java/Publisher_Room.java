import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

// Class Definition
public class Publisher_Room {

    // Topics
    public static final String TEMPERATURE_TOPIC = "Temperature"; // Topic for temperature readings
    public static final String HUMIDITY_TOPIC = "Humidity"; // Topic for humidity readings

    // Main Method
    public static void main(String[] args) throws MqttException, InterruptedException {
        MqttClient client = new MqttClient("tcp://localhost:1883", MqttClient.generateClientId()); // Create MQTT client
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setWill("Floor and Room", "Client Disconnected".getBytes(), 2, true); // Set MQTT will message
        client.connect(mqttConnectOptions); // Connect to MQTT broker

        Thread room = new Thread(() -> {
            Random random = new Random();
            DecimalFormat decimalFormat = new DecimalFormat(".00");
            while (!Thread.currentThread().isInterrupted()) {
                int roomNumber = random.nextInt(10); // Generate a random room number
                double roomTemperature = ThreadLocalRandom.current().nextDouble(15.0, 35.0); // Generate a random room temperature between 15.0 and 35.0

                String temperatureMsg = "\n"+ "----- Checking Room " + roomNumber +  " -----" + "\nTemperature ➤ " + decimalFormat.format(roomTemperature) + "ºC";
                MqttMessage temperatureMqttMsg = new MqttMessage(temperatureMsg.getBytes());
                temperatureMqttMsg.setRetained(true); // Retain the temperature message
                try {
                    client.publish(TEMPERATURE_TOPIC, temperatureMqttMsg); // Publish temperature message to the temperature topic
                    System.out.println(temperatureMsg);

                    double roomHumidity = ThreadLocalRandom.current().nextDouble(0.0, 100.0); // Generate a random room humidity between 0.0 and 100.0
                    String humidityMsg = "Humidity ➤ " + decimalFormat.format(roomHumidity) + "%\n";
                    MqttMessage humidityMqttMsg = new MqttMessage(humidityMsg.getBytes());
                    humidityMqttMsg.setRetained(true); // Retain the humidity message
                    client.publish(HUMIDITY_TOPIC, humidityMqttMsg); // Publish humidity message to the humidity topic
                    System.out.println(humidityMsg);

                    Thread.sleep(1000); // Wait for 1000 milliseconds
                } catch (InterruptedException | MqttException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt(); // Interrupt the thread if an exception occurs
                }
            }
            try {
                client.disconnect(); // Disconnect from the MQTT broker
            } catch (MqttException e) {
                e.printStackTrace();
            }
        });

        room.start(); // Start the room thread

        try {
            room.join(); // Wait for the room thread to finish
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
