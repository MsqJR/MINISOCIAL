package Messaging;

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.ObjectMessage;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Message-Driven Bean that handles notifications sent to the NotificationQueue
 */
@MessageDriven(
        activationConfig = {
                @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:/jms/queue/NotificationQueue"),
                @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue"),
                @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")
        }
)
public class NotificationMDB implements MessageListener {

    private static final Logger logger = Logger.getLogger(NotificationMDB.class.getName());

    public NotificationMDB() {
        logger.info("NotificationMDB initialized");
    }

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof ObjectMessage) {
                ObjectMessage objectMessage = (ObjectMessage) message;
                Object object = objectMessage.getObject();

                if (object instanceof NotificationEvent) {
                    NotificationEvent event = (NotificationEvent) object;
                    processNotification(event);
                } else {
                    logger.warning("Received message is not a NotificationEvent: " + object);
                }
            } else {
                logger.warning("Received message is not an ObjectMessage: " + message);
            }
        } catch (JMSException e) {
            logger.log(Level.SEVERE, "Error processing notification message", e);
        }
    }

    private void processNotification(NotificationEvent event) {
        // Log the notification - in a real application, you might store it in a database or send via WebSockets
        logger.info("Received notification: " + event.toString());

        // Convert the notification to JSON format (for verification purposes)
        String jsonPayload = convertToJson(event);
        logger.info("Notification JSON: " + jsonPayload);

        // In a real application, you would implement additional processing here:
        // 1. Store the notification in a database
        // 2. Push to connected WebSocket clients for real-time updates
        // 3. Send emails or mobile push notifications for important events
    }

    /**
     * Simple method to create a JSON representation of the notification
     * In a production environment, use a proper JSON library like Jackson or Gson
     */
    private String convertToJson(NotificationEvent event) {
        return "{\n" +
                "  \"id\": \"" + event.getId() + "\",\n" +
                "  \"type\": \"" + event.getType() + "\",\n" +
                "  \"sender\": \"" + event.getSenderUsername() + "\",\n" +
                "  \"recipient\": \"" + event.getRecipientUsername() + "\",\n" +
                "  \"message\": \"" + event.getMessage() + "\",\n" +
                "  \"relatedEntityId\": " + event.getRelatedEntityId() + ",\n" +
                "  \"timestamp\": \"" + event.getTimestamp() + "\",\n" +
                "  \"read\": " + event.isRead() + "\n" +
            "}";
}
}