package Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.json.bind.annotation.JsonbTypeAdapter;
import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
public class Post {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long postId;

    @NotNull
    private String content;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "media_id")
    @JsonbTypeAdapter(MediaAttachementAdapter.class)
    private MediaAttachement media;

    public Post() {
        this.createdAt = LocalDateTime.now();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public MediaAttachement getMedia() {
        return media;
    }

    public void setMedia(MediaAttachement media) {
        this.media = media;
    }
    //
}