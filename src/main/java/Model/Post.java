package Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

@Entity
public class Post
{
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long PID;

    @NotNull
    private String content;

    @ManyToOne
    private User user;

    @OneToOne
    private MediaAttachement medattachement;

    public Post()
    {
    }
    public long getPID() {
        return PID;
    }
    public void setPID(long PID) {
        this.PID = PID;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

}
