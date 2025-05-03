package Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;

@Entity
public class Profile
{
    @Id
    @NotNull
    private long PID;

    @OneToOne(mappedBy = "user_profile")
    private User user;

    private String bio;
    public Profile() {
    }

    public String getBio() {
        return bio;
    }
    public void setBio(String bio)
    {
        this.bio = bio;
    }


}
