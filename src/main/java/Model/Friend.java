package Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

@Entity
public class Friend
{
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long FID;
    private String FriendName;
    private String FriendBio;

    @ManyToMany(mappedBy = "friends")
    private Set<User> users;

    public Friend() {
    }
    public String getFriendName() {
        return FriendName;
    }
    public void setFriendName(String FriendName) {
        this.FriendName = FriendName;
    }
    public long getFID() {
        return FID;
    }
    public void setFID(long FID) {
        this.FID = FID;
    }
    public String getFriendBio() {
        return FriendBio;
    }
    public void setFriendBio(String FriendBio) {
        this.FriendBio = FriendBio;
    }
    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}
