package Messaging;

import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.jms.*;

@Stateless
public class NotificationServiceBean implements NotificationService
{

    @Resource(mappedName = "java:/ConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(lookup = "java:/jms/queue/NotificationQueue")
    private Queue notificationQueue;

    @Override
    public void sendFriendRequestNotification(String sender, String recipient) {
        NotificationEvent event = new NotificationEvent(
                NotificationEvent.EventType.FRIEND_REQUEST_RECEIVED,
                sender,
                recipient,
                sender + " sent you a friend request"
        );
        sendJmsMessage(event);
    }

    @Override
    public void sendPostLikedNotification(String liker, String postOwner, Long postId) {
        NotificationEvent event = new NotificationEvent(
                NotificationEvent.EventType.POST_LIKED,
                liker,
                postOwner,
                liker + " liked your post",
                postId
        );
        sendJmsMessage(event);
    }

    @Override
    public void sendPostCommentedNotification(String commenter, String postOwner, Long postId, String comment) {
        String preview = comment.length() > 50 ? comment.substring(0, 47) + "..." : comment;
        NotificationEvent event = new NotificationEvent(
                NotificationEvent.EventType.POST_COMMENTED,
                commenter,
                postOwner,
                commenter + " commented: \"" + preview + "\"",
                postId
        );
        sendJmsMessage(event);
    }

    @Override
    public void sendGroupJoinLeaveNotification(String username, String groupName, boolean isJoin) {
        NotificationEvent event = new NotificationEvent(
                NotificationEvent.EventType.GROUP_JOIN_LEAVE,
                username,
                groupName,
                isJoin
        );
        sendJmsMessage(event);
    }

    private void sendJmsMessage(NotificationEvent event) {
        try (Connection connection = connectionFactory.createConnection();
             Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
             MessageProducer producer = session.createProducer(notificationQueue)) {

            ObjectMessage message = session.createObjectMessage(event);
            producer.send(message);

        } catch (JMSException e) {
            throw new RuntimeException("Failed to send JMS message", e);
        }
    }
}