package EJBs;

import Model.Group;
import Model.Post;
import Model.User;
import Service.GroupService;
import Service.NotificationService;
import Service.UserService;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;

@Stateless
public class GroupServiceBean implements GroupService {

    @PersistenceContext
    private EntityManager em;

    @EJB
    private NotificationService notificationService;

    @EJB(name = "UserServiceBean")
    private UserService userService;

    @Override
    public void createGroup(long userid, String groupName, String groupDescription, String groupType) {
        User userCreator = em.find(User.class, userid);
        if (userCreator == null) {
            throw new RuntimeException("User not found with ID: " + userid);
        }

        Group newGroup = new Group(groupName, groupDescription, groupType, userCreator.getName());
        newGroup.setAdmins(newGroup.addToAdminsList(userCreator));
        em.persist(newGroup);
    }

    @Override
    public void joinGroupRequest(String username, long userid, String groupname, String groupType) {
        User user = userService.findUserById(userid);
        Group group = findGroupByName(groupname);

        if (user == null || group == null) {
            throw new RuntimeException("User or Group not found.");
        }

        if (groupType.equalsIgnoreCase("public")) {
            group.addToUsersList(user);
            notificationService.sendJoinNotification(username, groupname);
            em.merge(group);
        } else if (groupType.equalsIgnoreCase("private")) {
            group.setWaitingUsersList(group.addToWaitingUsersList(user));
            em.merge(group);
        }
    }

    @Override
    public void leaveGroup(String username, int userid, String groupname) {
        User user = userService.findUserById(userid);
        Group group = findGroupByName(groupname);

        if (user == null || group == null) {
            throw new RuntimeException("User or Group not found.");
        }

        group.setUsers(group.removeFromUsersList(user));
        notificationService.sendLeaveNotification(username, groupname);
        em.merge(group);
    }

    @Override
    public Group findGroupByName(String groupname) {
        try {
            TypedQuery<Group> query = em.createQuery("SELECT g FROM Group g WHERE g.groupname = :groupname", Group.class);
            query.setParameter("groupname", groupname);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public void PromoteUserToBeAdmin(String usercreator, String username, long userid, String groupname) {
        User user = userService.findUserById(userid);
        Group group = findGroupByName(groupname);

        if (group == null || user == null) {
            throw new RuntimeException("Group or User not found.");
        }

        if (!usercreator.equals(group.getGroupcreator())) {
            System.out.println("You are not the group creator");
        } else {
            group.setAdmins(group.addToAdminsList(user));
            group.setUsers(group.removeFromUsersList(user));
            em.merge(group);
            System.out.println("User promoted to admin");
        }
    }

    @Override
    public void removeGroup(String usercreator, String groupname) {
        Group group = findGroupByName(groupname);

        if (group == null) {
            throw new RuntimeException("Group not found");
        }

        if (!usercreator.equals(group.getGroupcreator())) {
            System.out.println("You are not the group creator");
        } else {
            em.remove(em.contains(group) ? group : em.merge(group));
            System.out.println("Group removed");
        }
    }

    @Override
    public List<Group> getAllGroups() {
        try {
            TypedQuery<Group> query = em.createQuery("SELECT g FROM Group g", Group.class);
            return query.getResultList();
        } catch (NoResultException e) {
            return new java.util.ArrayList<>();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving all groups", e);
        }
    }

    @Override
    public void addpost(String username, String groupName, String content, String imageUrl, String link) {
        // Implementation pending: integrate with PostServiceBean when ready
    }
}