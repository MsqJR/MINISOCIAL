package Messaging;

public interface NotificationService {
    void sendFriendRequestNotification(String sender, String recipient);
    void sendPostLikedNotification(String liker, String postOwner, Long postId);
    void sendPostCommentedNotification(String commenter, String postOwner, Long postId, String comment);
    void sendGroupJoinLeaveNotification(String username, String groupName, boolean isJoin);
}