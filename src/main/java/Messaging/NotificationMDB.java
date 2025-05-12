package Messaging;

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.jms.*;

@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:/jms/queue/NotificationQueue"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue")
})
public class NotificationMDB implements MessageListener {

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof ObjectMessage) {
                ObjectMessage objMessage = (ObjectMessage) message;
                NotificationEvent event = (NotificationEvent) objMessage.getObject();

                // Process the notification
                System.out.println("Received Notification Event:");
                System.out.println(event.toJson());

                // Here you would typically:
                // 1. Store in database
                // 2. Push to WebSocket
                // 3. Send email/SMS if needed
            }
        } catch (JMSException e) {
            throw new RuntimeException("Error processing JMS message", e);
        }
    }
}