package Messaging;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A standardized event object that can accommodate various notification event types
 * for the social media application.
 */
public class NotificationEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    // Type of notification event
    public enum EventType {
        FRIEND_REQUEST_RECEIVED,
        POST_LIKED,
        POST_COMMENTED,
        FRIEND_REQUEST_ACCEPTED,
        FRIEND_REQUEST_REJECTED
    }

    private String id;
    private EventType type;
    private String senderUsername;
    private String recipientUsername;
    private String message;
    private Long relatedEntityId; // Post ID, comment ID, etc.
    private LocalDateTime timestamp;
    private boolean read;

    // Default constructor required for JMS
    public NotificationEvent() {
        this.timestamp = LocalDateTime.now();
        this.read = false;
    }

    public NotificationEvent(EventType type, String senderUsername, String recipientUsername, String message) {
        this();
        this.type = type;
        this.senderUsername = senderUsername;
        this.recipientUsername = recipientUsername;
        this.message = message;
    }

    public NotificationEvent(EventType type, String senderUsername, String recipientUsername, String message, Long relatedEntityId) {
        this(type, senderUsername, recipientUsername, message);
        this.relatedEntityId = relatedEntityId;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String getRecipientUsername() {
        return recipientUsername;
    }

    public void setRecipientUsername(String recipientUsername) {
        this.recipientUsername = recipientUsername;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getRelatedEntityId() {
        return relatedEntityId;
    }

    public void setRelatedEntityId(Long relatedEntityId) {
        this.relatedEntityId = relatedEntityId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    @Override
    public String toString() {
        return "NotificationEvent{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", sender='" + senderUsername + '\'' +
                ", recipient='" + recipientUsername + '\'' +
                ", message='" + message + '\'' +
                ", relatedEntityId=" + relatedEntityId +
                ", timestamp=" + timestamp +
                ", read=" + read +
            '}';
}
}