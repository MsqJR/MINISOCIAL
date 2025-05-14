package Model;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "profiles")
public class Profile
{
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long PID;

    @OneToOne(mappedBy = "user_profile")
    @JsonbTransient
    private User user;

    private String profile_name;
    private String profile_email;
    private String profile_password;
    private String profile_role;
    private String bio;
    public Profile() {
    }

    public Profile(String profile_name, String profile_email, String profile_password, String profile_role, String bio) {
        this.PID = PID;
        this.user = user;
        this.profile_name = profile_name;
        this.profile_email = profile_email;
        this.profile_password = profile_password;
        this.profile_role = profile_role;
        this.bio = bio;
    }

    public String getBio() {
        return bio;
    }
    public void setBio(String bio)
    {
        this.bio = bio;
    }

    public long getPID() {
        return PID;
    }

    public void setPID(long PID) {
        this.PID = PID;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getProfile_email() {
        return profile_email;
    }

    public void setProfile_email(String profile_email) {
        this.profile_email = profile_email;
    }

    public String getProfile_name() {
        return profile_name;
    }

    public void setProfile_name(String profile_name) {
        this.profile_name = profile_name;
    }

    public String getProfile_role() {
        return profile_role;
    }

    public void setProfile_role(String profile_role) {
        this.profile_role = profile_role;
    }

    public String getProfile_password() {
        return profile_password;
    }

    public void setProfile_password(String profile_password) {
        this.profile_password = profile_password;
    }
}
