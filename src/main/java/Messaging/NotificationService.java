package Messaging;

public interface NotificationService {

    void sendNotification(NotificationEvent event);

    void sendFriendRequestNotification(String senderUsername, String recipientUsername);

    void sendPostLikedNotification(String likerUsername, String postOwnerUsername, Long postId);

    void sendPostCommentedNotification(String commenterUsername, String postOwnerUsername, Long postId, String commentText);

    void sendFriendRequestAcceptedNotification(String accepterUsername, String requesterUsername);

    void sendFriendRequestRejectedNotification(String rejecterUsername, String requesterUsername);
}