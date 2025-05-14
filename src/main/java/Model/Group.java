package Model;


import EJBs.UserServiceBean;
import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "groups")
public class Group {
    @Transient
    private UserServiceBean USB ;
    @JsonbTransient
    public User getCreator() {
        return admins.isEmpty() ? null : admins.get(0);
    }

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
    @JoinTable(
            name = "group_users",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> users = new ArrayList<>();

    @JsonbTransient
    @ManyToMany
    @JoinTable(
            name = "group_admins",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "admin_id")
    )
    private List<User> admins = new ArrayList<>();



    private List<String> groupJoinrequests = new ArrayList<>();

    @JsonbTransient
    @ManyToMany
    @JoinTable(
            name = "group_waiting_users",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "waiting_user_id")
    )
    private List<User> waitingUsersList = new ArrayList<>();

    private String groupcreator;


    public Group() {}
    public Group(String groupname, String groupDescription,String groupType,String usercreator) {
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
    public void setUsers(List<User> users)
    {
        this.users= users;
    }

    public String getGroupcreator() {return groupcreator;}
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
    public List<User> removeFromWaitingList(User user) {
        waitingUsersList.remove(user);
        return waitingUsersList;
    }
    public List<User> removeFromAdminList(User user) {


        if (getAdmins().contains(user))
        {
            admins.remove(user);
            return admins;
        }
        else {
            throw new IllegalArgumentException("User is not an admin");
        }

    }
    public List<User> addToWaitingUsersList(User user){
        waitingUsersList.add(user);
        return waitingUsersList;
    }
    public void setGroupname(String groupname){
        this.groupname = groupname;
    }
    public String getGroupType() {return groupType;}
    public String getGroupDescription() {return groupDescription;}
    public void setGroupDescription(String groupDescription) {this.groupDescription = groupDescription;}
    public int getGroupId() {return GroupId;}
    public void setGroupId(int groupId) {GroupId = groupId;}
    public String getGroupName() {return groupname;}



    public List<User> getUsers() {
        return users;
    }

    public List<User> getAdmins() {
        return admins;
}

    public String getGroupname() {
        return groupname;
    }

    public List<String> getGroupJoinrequests() {
        return groupJoinrequests;
    }

    public void setGroupJoinrequests(List<String> groupJoinrequests) {
        this.groupJoinrequests = groupJoinrequests;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public List<User> getWaitingUsersList() {
        return waitingUsersList;
    }

    public void setGroupcreator(String groupcreator) {
        this.groupcreator = groupcreator;
    }
}