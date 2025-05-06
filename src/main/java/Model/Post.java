package Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.json.bind.annotation.JsonbTypeAdapter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "posts")
public class Post implements Serializable
{
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

    @ManyToMany
    @JoinTable
              (
              name = "post_likes",
             joinColumns = @JoinColumn(name = "post_id"),
             inverseJoinColumns = @JoinColumn(name = "user_id")
            )
    private Set<User> userlikes = new HashSet<>();
/******************************************************************************/
    @ManyToMany
    @JoinTable
            (
                    name = "post_comments",
                    joinColumns = @JoinColumn(name = "post_id"),
                    inverseJoinColumns = @JoinColumn(name = "user_id")
            )
    private Set<User> userComments = new HashSet<>();
/********************************************************************************************/

    public Post() {
        this.createdAt = LocalDateTime.now();
    }
    public Post(long PID,String content, User user, MediaAttachement media) {
        this.content = content;
        this.user = user;
        this.media = media;
        this.postId = PID;
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

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }
   /*************************************************************************/
   public void SetLikes(Set<User> likes) {
        this.userlikes = likes;
   }
   public Set<User> getLikes() {
       return userlikes;
   }
  /******************************************************************************/
  public void SetComments(Set<User> comments)
  {
        this.userComments = comments;
   }
   public Set<User> getComments()
   {
       return userComments;
   }

}