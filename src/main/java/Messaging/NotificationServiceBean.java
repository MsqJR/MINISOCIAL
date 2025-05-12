package Messaging;

import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import jakarta.jms.MessageProducer;
import jakarta.jms.ObjectMessage;
import jakarta.jms.Queue;
import jakarta.jms.Session;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of the NotificationService that uses JMS to send notifications
 */
@Stateless
public class NotificationServiceBean implements NotificationService {

    private static final Logger logger = Logger.getLogger(NotificationServiceBean.class.getName());

    @Resource(mappedName = "java:/ConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(lookup = "java:/jms/queue/NotificationQueue")
    private Queue notificationQueue;

    @Override
    public void sendNotification(NotificationEvent event) {
        // Generate a unique ID for the notification
        event.setId(UUID.randomUUID().toString());

        Connection connection = null;
        try {
            connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(notificationQueue);

            // Create and send the message
            ObjectMessage message = session.createObjectMessage(event);
            producer.send(message);

            logger.info("Sent notification: " + event.toString());
        } catch (JMSException e) {
            logger.log(Level.SEVERE, "Error sending notification", e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    logger.log(Level.WARNING, "Error closing JMS connection", e);
                }
            }
        }
    }

    @Override
    public void sendFriendRequestNotification(String senderUsername, String recipientUsername) {
        String message = senderUsername + " has sent you a friend request";
        NotificationEvent event = new NotificationEvent(
                NotificationEvent.EventType.FRIEND_REQUEST_RECEIVED,
                senderUsername,
                recipientUsername,
                message
        );
        sendNotification(event);
    }

    @Override
    public void sendPostLikedNotification(String likerUsername, String postOwnerUsername, Long postId) {
        String message = likerUsername + " liked your post";
        NotificationEvent event = new NotificationEvent(
                NotificationEvent.EventType.POST_LIKED,
                likerUsername,
                postOwnerUsername,
                message,
                postId
        );
        sendNotification(event);
    }

    @Override
    public void sendPostCommentedNotification(String commenterUsername, String postOwnerUsername, Long postId, String commentText) {
        // Truncate comment text if it's too long
        String commentPreview = commentText.length() > 50 ? commentText.substring(0, 47) + "..." : commentText;
        String message = commenterUsername + " commented on your post: \"" + commentPreview + "\"";

        NotificationEvent event = new NotificationEvent(
                NotificationEvent.EventType.POST_COMMENTED,
                commenterUsername,
                postOwnerUsername,
                message,
                postId
        );
        sendNotification(event);
    }

    @Override
    public void sendFriendRequestAcceptedNotification(String accepterUsername, String requesterUsername) {
        String message = accepterUsername + " accepted your friend request";
        NotificationEvent event = new NotificationEvent(
                NotificationEvent.EventType.FRIEND_REQUEST_ACCEPTED,
                accepterUsername,
                requesterUsername,
                message
        );
        sendNotification(event);
    }

    @Override
    public void sendFriendRequestRejectedNotification(String rejecterUsername, String requesterUsername) {
        String message = rejecterUsername + " rejected your friend request";
        NotificationEvent event = new NotificationEvent(
                NotificationEvent.EventType.FRIEND_REQUEST_REJECTED,
                rejecterUsername,
                requesterUsername,
                message
        );
        sendNotification(event);
    }
}