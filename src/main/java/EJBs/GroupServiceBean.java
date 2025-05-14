package EJBs;

import Model.Group;
import Model.Post;
import Model.User;
import Service.GroupService;
import Service.NotificationService;
import Service.PostService;
import Service.UserService;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class GroupServiceBean implements GroupService
{

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

        if (group.getUsers().contains(user) || group.getAdmins().contains(user)) {
            throw new RuntimeException("User is already a member of this group.");
        }
        if (groupType.equals("private") && group.getWaitingUsersList().contains(user)) {
            throw new RuntimeException("Join request already sent for this private group.");
        }

        if (groupType.equals("public"))
        {
            group.setUsers(group.addToUsersList(user));
            em.merge(group);
            System.out.println("Successfully joined public group");
            notificationService.sendJoinNotification(username, groupname);
        } else if (groupType.equals("private")) {
            group.setWaitingUsersList(group.addToWaitingUsersList(user));
            em.merge(group);
            System.out.println("Request sent to join private group: " + groupname);
        } else {
            throw new RuntimeException("Invalid group type. Must be 'public' or 'private'.");
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
            throw new RuntimeException("You are not the group creator");
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
            throw new RuntimeException("You are not the group creator");
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

    @EJB
    private PostService postService;

    @Override
    public void addpost(String username, String groupName, String content, String imageUrl, String link) {
        Group group = findGroupByName(groupName);
        User user = userService.findUserByName(username);

        if (group == null || user == null) {
            throw new RuntimeException("Group or User not found.");
        }

        if (!group.getUsers().contains(user) && !group.getAdmins().contains(user)) {
            throw new RuntimeException("User is not a member of this group.");
        }

        postService.createGroupPost(user, group, content, imageUrl, link);
    }

    @Override
    public void removePostFromGroup(String adminUsername, String groupName, long postId) {
        Group group = findGroupByName(groupName);
        User admin = userService.findUserByName(adminUsername);

        if (group == null || admin == null) {
            throw new RuntimeException("Group or Admin not found.");
        }

        if (!group.getAdmins().contains(admin)) {
            throw new RuntimeException("User is not an admin of this group.");
        }

        Post post = postService.findPostById(postId);
        if (post == null || !post.getGroup().equals(group)) {
            throw new RuntimeException("Post not found in this group.");
        }

        postService.deletePost(postId);
    }

    @Override
    public void removeUserFromGroup(String adminUsername, String targetUsername, String groupName) {
        Group group = findGroupByName(groupName);
        User admin = userService.findUserByName(adminUsername);
        User targetUser = userService.findUserByName(targetUsername);

        if (group == null || admin == null || targetUser == null) {
            throw new RuntimeException("Group, Admin, or User not found.");
        }

        if (!group.getAdmins().contains(admin)) {
            throw new RuntimeException("User is not an admin of this group.");
        }

        if (!group.getUsers().contains(targetUser) && !group.getAdmins().contains(targetUser)) {
            throw new RuntimeException("Target user is not a member of this group.");
        }

        if (group.getAdmins().contains(targetUser)) {
            group.getAdmins().remove(targetUser);
        } else {
            group.getUsers().remove(targetUser);
        }

        em.merge(group);
    }

    @Override
    public void acceptJoinRequest(String username, long userid, String groupname) {
        User user = em.find(User.class, userid);
        Group group = findGroupByName(groupname);

        if (user == null || group == null) {
            throw new RuntimeException("User or Group not found.");
        }

        if (!group.getGroupType().equals("private")) {
            throw new RuntimeException("This is not a private group.");
        }

        if (group.getWaitingUsersList().contains(user)) {
            group.setUsers(group.addToUsersList(user));
            group.setWaitingUsersList(group.removeFromWaitingList(user));
            em.merge(group);
            System.out.println("User " + username + "'s join request for private group " + groupname + " accepted.");
            notificationService.sendJoinNotification(username, groupname);
        } else {
            throw new RuntimeException("User is not in the waiting list for this private group.");
        }
    }

    @Override
    public List<User> getUsersInGroup(String groupName) {
        Group group = findGroupByName(groupName);
        if (group == null) {
            throw new RuntimeException("Group not found.");
        }

        List<User> allUsers = new ArrayList<>();
        allUsers.addAll(group.getUsers());
        allUsers.addAll(group.getAdmins());

        return allUsers;
    }
    @Override
    public List<String> getWaitingUsernamesForGroup(String groupName) {
        Group group = findGroupByName(groupName);
        if (group == null) {
            throw new RuntimeException("Group not found.");
        }

        return group.getWaitingUsersList().stream()
                .map(User::getName)
                .collect(Collectors.toList());
    }
}