package Model;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.json.bind.annotation.JsonbTypeAdapter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
public class Post {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long postId;

    @NotNull
    private String content;
    @ManyToOne(optional = true)
    @JoinColumn(name = "group_id")
    private Group group;
    public Group getGroup() {return group;}
    public void setGroup(Group group) {this.group = group;}
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;  // Added this field to match the mappedBy in Group class

    @OneToOne
    @JoinColumn(name = "media_id")
    @JsonbTypeAdapter(MediaAttachementAdapter.class)
    private MediaAttachement media;

    @JsonbTransient
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @JsonbTransient
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    public Post() {
        this.createdAt = LocalDateTime.now();
    }

    public Post(String content, User user, MediaAttachement media) {
        this.content = content;
        this.user = user;
        this.media = media;
        this.createdAt = LocalDateTime.now();
    }

    public Post(String content, User user, Group group, MediaAttachement media) {
        this.content = content;
        this.user = user;
        this.group = group;
        this.media = media;
        this.createdAt = LocalDateTime.now();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public MediaAttachement getMedia() {
        return media;
    }

    public void setMedia(MediaAttachement media) {
        this.media = media;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Like> getLikes() {
        return likes;
    }

    public void setLikes(List<Like> likes) {
        this.likes = likes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}