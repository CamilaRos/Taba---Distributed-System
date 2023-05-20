import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import java.util.Random;

// Class Definition
public class Publisher_Floor {

    // Topics
    public static final String LIGHT_TOPIC = "floor/light/id"; // Topic for light status
    public static final String WINDOW_TOPIC = "floor/window/location"; // Topic for window status

    // Random constant Lists
    public static final String[] FLOOR_NUM = {"Ground Floor", "First Floor", "Second Floor", "Third Floor"}; // Possible floor names
    public static final String[] LIGHT_STATUS = {"On", "Off"}; // Possible light statuses
    public static final String[] LIGHT_ID = {"Ceiling", "Side", "Table"}; // Possible light IDs
    public static final String[] WINDOW_NAME = {"Window 1", "Window 2", "Window 3", "Window 4"}; // Possible window names
    public static final String[] WINDOW_STATUS = {"Open", "Closed"}; // Possible window statuses

    // Main Method
    public static void main(String[] args) throws MqttException {
        MqttClient client = new MqttClient("tcp://localhost:1883", MqttClient.generateClientId()); // Create MQTT client
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setWill("floor/room/will", "Client Disconnected".getBytes(), 2, true); // Set MQTT will message
        client.connect(mqttConnectOptions); // Connect to MQTT broker

        Thread floor = new Thread(() -> {
            Random random = new Random();
            while (!Thread.currentThread().isInterrupted()) {
                // Create random values for floor, light status, light ID, window, and window status
                int rndFloor = random.nextInt(FLOOR_NUM.length);
                int rndLightStatus = random.nextInt(LIGHT_STATUS.length);
                int rndLightID = random.nextInt(LIGHT_ID.length);
                int rndWindows = random.nextInt(WINDOW_NAME.length);
                int rndWindowStatus = random.nextInt(WINDOW_STATUS.length);

                // Create MQTT messages for floor, light status, light ID, window, and window status
                String floorMsg = "----- Checking " + FLOOR_NUM[rndFloor] + " -----";
                MqttMessage floors = new MqttMessage(floorMsg.getBytes());

                String lightMsg = "Status: " + LIGHT_STATUS[rndLightStatus];
                MqttMessage light = new MqttMessage(lightMsg.getBytes());
                light.setRetained(true); // Retain the light status message

                String idMsg = "The lights at " + LIGHT_ID[rndLightID];
                MqttMessage id = new MqttMessage(idMsg.getBytes());
                id.setRetained(true); // Retain the light ID message

                String windowIDMsg = WINDOW_NAME[rndWindows] + " checked" + " ✓";;
                MqttMessage windows = new MqttMessage(windowIDMsg.getBytes());

                String windowStatus = "Window status: " + WINDOW_STATUS[rndWindowStatus];
                MqttMessage windowSt = new MqttMessage(windowStatus.getBytes());

                try {
                    // Publish MQTT messages to respective topics and print the messages
                    client.publish(LIGHT_TOPIC, floors);
                    System.out.println("\n" + floorMsg);

                    client.publish(LIGHT_TOPIC, id);
                    System.out.println(idMsg);

                    client.publish(LIGHT_TOPIC, light);
                    System.out.println(lightMsg);

                    client.publish(WINDOW_TOPIC, windows);
                    System.out.println(windowIDMsg);

                    client.publish(WINDOW_TOPIC, windowSt);
                    System.out.println(windowStatus);

                    Thread.sleep(500); // Wait for 500 milliseconds
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

        floor.start(); // Start the floor thread

        try {
            floor.join(); // Wait for the floor thread to finish
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
