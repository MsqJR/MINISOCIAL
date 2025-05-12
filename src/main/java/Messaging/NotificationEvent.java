package Messaging;

import java.io.Serializable;
import java.time.LocalDateTime;

public class NotificationEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum EventType {
        FRIEND_REQUEST_RECEIVED,
        POST_LIKED,
        POST_COMMENTED,
        GROUP_JOIN_LEAVE
    }

    private String eventName;
    private EventType type;
    private String sender;
    private String recipient;
    private String message;
    private Long postId; // For post-related events
    private String groupName; // For group join/leave events
    private LocalDateTime timestamp;

    public NotificationEvent() {
        this.timestamp = LocalDateTime.now();
    }

    public NotificationEvent(EventType type, String sender, String recipient, String message) {
        this();
        this.type = type;
        this.sender = sender;
        this.recipient = recipient;
        this.message = message;
        this.eventName = type.name();
    }


    public NotificationEvent(EventType type, String sender, String recipient, String message, Long postId) {
        this(type, sender, recipient, message);
        this.postId = postId;
    }

    public NotificationEvent(EventType type, String sender, String groupName, boolean isJoin) {
        this();
        this.type = type;
        this.sender = sender;
        this.groupName = groupName;
        this.eventName = type.name();
        this.message = sender + " has " + (isJoin ? "joined" : "left") + " group " + groupName;
    }


    public String toJson() {
        return String.format(
                "{\"eventName\":\"%s\",\"type\":\"%s\",\"sender\":\"%s\",\"recipient\":\"%s\"," +
                        "\"message\":\"%s\",\"postId\":%s,\"groupName\":%s,\"timestamp\":\"%s\"}",
                eventName, type, sender, recipient, message,
                postId != null ? "\"" + postId + "\"" : "null",
                groupName != null ? "\"" + groupName + "\"" : "null",
                timestamp
        );
    }

    @Override
    public String toString() {
        return toJson();
    }
}