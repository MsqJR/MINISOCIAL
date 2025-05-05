package Model;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import Model.Post;

@Entity
@Table(name = "users")
public class User {


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

    public User() {}

    public User(String Name, String email, String Password, String role) {
        this.Name = Name;
        this.email = email;
        this.Password = Password;
        this.Role = role;
    }
    @JsonbTransient
    @ManyToMany
    @JoinTable(name = "pending_requests",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "requester_id"))
    private Set<User> friendRequests = new HashSet<>();
/*************************************************************************/
    @JsonbTransient
    @ManyToMany
    @JoinTable(name = "user_friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id"))
    private Set<User> friends = new HashSet<>();
    /*************************************************************************/
    @OneToOne
    private Profile user_profile;
    /*************************************************************************/

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

    public Set<User> getFriendRequests() {
        return friendRequests;
    }

    public void setFriendRequests(Set<User> friendRequests) {
        this.friendRequests = friendRequests;
    }

    public Set<User> getFriends() {
        return friends;
    }

    public void setFriends(Set<User> friends) {
        this.friends = friends;
    }
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Post> posts = new ArrayList<>();
    public List<Post> getPosts() {
        return posts;
    }



}
