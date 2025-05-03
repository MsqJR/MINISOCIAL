package Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user")
public class User
{
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long ID;
    private String Name;

    @Email
    @NotNull
    private String email;
    @NotNull
    private String Password;
    private String Role;
    private int num_friends=0;


    public User() {
    }

    public User(String Name, String email, String Password, String role) {
        this.Name = Name;
        this.email = email;
        this.Password = Password;
        this.Role = role;
    }

    @ManyToMany
    @JoinTable(name = "pending_requests",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "requester_id"))
    private Set<User> friendRequests = new HashSet<>();
/*****************************************************************************************/
    @ManyToMany
    @JoinTable(name = "user_friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id"))
    private Set<User> Friends = new HashSet<>();
/*****************************************************************************************/
@ManyToMany
@JoinTable(name = "Send_requests",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "receiver_id"))
private Set<User> Sender = new HashSet<>();
/*****************************************************************************************/
@OneToOne
private Profile user_profile;
/********************************************************************************/
    public Set<User> getFreinds()
    {
        return Friends;
    }

    public void setFreinds(Set<User> freinds) {
        this.Friends = freinds;
    }

    public long getId() {
        return ID;
    }

    public void setId(long id) {
        ID = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = role;
    }

    public int getNum_friends() {
        return num_friends;
    }
    public void setNum_friends(int num_friends) {
        this.num_friends = num_friends;
    }

    public Set<User> getFriends() {
        return Friends;
    }

    public void setFriends(Set<User> friends) {
        Friends = friends;
    }

    public Set<User> getFriendRequests() {
        return friendRequests;
    }

    public void setFriendRequests(Set<User> friendRequests) {
        this.friendRequests = friendRequests;
    }
    public void setFriendRequests(User friendRequester)
    {
        friendRequests.add(friendRequester);

    }
    public Set<User> getSender() {
        return Sender;
    }
    public void setSender(User sender)
    {
        Sender.add(sender);
    }
}