package Service;

import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.jms.*;

@Stateless
public class groupNotifications {

    @Resource(mappedName = "java:/ConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(lookup = "java:/jms/GroupNotificationTopic")
    private Topic groupNotificationTopic;

    public void sendJoinNotification(String username, String groupName) {
        String message = String.format("User %s has joined the group %s", username, groupName);
        sendJmsMessage(message, "JOIN");
    }

    public void sendLeaveNotification(String username, String groupName) {
        String message = String.format("User %s has left the group %s", username, groupName);
        sendJmsMessage(message, "LEAVE");
    }

    public void sendApprovalNotification(String username, String groupName) {
        String message = String.format("User %s has been approved to join the group %s", username, groupName);
        sendJmsMessage(message, "APPROVAL");
    }

    private void sendJmsMessage(String content, String notificationType) {
        try (Connection connection = connectionFactory.createConnection();
             Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
             MessageProducer producer = session.createProducer(groupNotificationTopic)) {

            TextMessage message = session.createTextMessage(content);
            message.setStringProperty("notificationType", notificationType);
            producer.send(message);

        } catch (JMSException e) {
            throw new RuntimeException("Failed to send JMS message", e);
        }
    }
}
