package Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class Profile
{

    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long PID;

    @OneToOne(mappedBy = "profile")
    private User user;
    @OneToOne(mappedBy = "profile")
    private Friend friend;
    public Profile() {
    }
}
