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
    private String bio;
    private int num_friends=0;

    @ManyToMany
    @JoinTable(
            name = "user_friend",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private Set<Friend> friends = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "user_pending_requests",
            joinColumns = @JoinColumn(name = "receiver_id"),
            inverseJoinColumns = @JoinColumn(name = "sender_id"))
    private Set<User> pendingRequests = new HashSet<>();


    @OneToOne
    private Profile profile;

    public User() {
    }

    public User(String Name, String email, String Password, String role) {
        this.Name = Name;
        this.email = email;
        this.Password = Password;
        this.Role = role;
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

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
    public Set<Friend> getFriends() {
        return friends;
    }

    public void setFriends(Set<Friend> friends) {
        this.friends = friends;
    }
    public int getNum_friends() {
        return num_friends;
    }
    public void setNum_friends(int num_friends) {
        this.num_friends = num_friends;
    }
    public Set<User> getPendingRequests() {
        return pendingRequests;
    }

    public void setPendingRequests(Set<User> pendingRequests) {
        this.pendingRequests = pendingRequests;
    }
}