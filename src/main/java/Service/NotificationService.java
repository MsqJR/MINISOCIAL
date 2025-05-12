package Service;

import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSProducer;
import jakarta.jms.Topic;

@Stateless
public class NotificationService {
    @Resource(lookup = "java:/jms/GroupNotificationTopic")
    private Topic groupNotificationTopic;

    @Inject
    private JMSContext jmsContext;

    public NotificationService() {}
    public void sendJoinNotification(String username, String groupName) {
        String message = String.format("User %s has joined the group %s", username, groupName);
        JMSProducer producer = jmsContext.createProducer();
        producer.setProperty("notificationType", "JOIN");
        producer.send(groupNotificationTopic, message);
    }

    public void sendLeaveNotification(String username, String groupName) {
        String message = String.format("User %s has left the group %s", username, groupName);
        JMSProducer producer = jmsContext.createProducer();
        producer.setProperty("notificationType", "LEAVE");
        producer.send(groupNotificationTopic, message);
    }

    public void sendApprovalNotification(String username, String groupName) {
        String message = String.format("User %s has been approved to join the group %s", username, groupName);
        JMSProducer producer = jmsContext.createProducer();
        producer.setProperty("notificationType", "APPROVAL");
        producer.send(groupNotificationTopic, message);
    }

}
