package Model;


import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "groups")
public class Group {
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int GroupId;

    @NotNull
    @Column(name="groupname")
    private String groupname;

    @Column(name="group_description")
    private String groupDescription;

    @Column(name="group_type")
    private String groupType;

    @JsonbTransient
    @ManyToMany
    private List<User> users = new ArrayList<>();

    @JsonbTransient
    @ManyToMany
    private List<User> admins = new ArrayList<>();

    @JsonbTransient
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    private List<String> groupJoinrequests = new ArrayList<>();

    @JsonbTransient
    @ManyToMany
    private List<User> waitingUsersList = new ArrayList<>();

    private String groupcreator;


    public Group() {}
    public Group(String usercreator,String groupname, String groupDescription,String groupType) {
        this.groupname = groupname;
        this.groupDescription = groupDescription;
        this.groupType = groupType;
        this.groupcreator = usercreator;

    }

    public void setWaitingUsersList(List<User> waitingUsersList){
        this.waitingUsersList = waitingUsersList;
    }


    public void setAdmins(List<User> admins) {
        this.admins = admins;
    }
    public void setUsers(List<User> users) {
        this.users = users;
    }

    public User getCreator() {
        return admins.get(0);
    }
    public List<User> addToAdminsList(User user) {
        admins.add(user);
        return admins;
    }

    public List<User> addToUsersList(User user) {
        users.add(user);
        return users;
    }
    public List<User> removeFromUsersList(User user) {
        users.remove(user);
        return users;
    }
    public List<User> addToWaitingUsersList(User user){
        waitingUsersList.add(user);
        return waitingUsersList;
    }

    public String getGroupDescription() {return groupDescription;}
    public void setGroupDescription(String groupDescription) {this.groupDescription = groupDescription;}
    public int getGroupId() {return GroupId;}
    public void setGroupId(int groupId) {GroupId = groupId;}
    public String getGroupName() {return groupname;}
    public String getGroupcreator() {return groupcreator;}


}