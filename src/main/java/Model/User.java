package Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

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
}