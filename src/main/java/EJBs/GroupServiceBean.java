package EJBs;

import Model.Group;
import Model.Post;
import Model.User;
import Service.GroupService;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Stateless
public class GroupServiceBean implements GroupService {
    @PersistenceContext
    private EntityManager em;

    @Override
    public void createGroup(String usercreator, long userid, String groupName, String groupDescription, String groupType) {
        User userCreator = em.find(User.class, userid);
        if (userCreator == null) {
            throw new RuntimeException("User not found with ID: " + userid);
        }

        Group newgroup = new Group(usercreator, groupName, groupDescription, groupType);
        newgroup.addToAdminsList(userCreator);
        newgroup.addToUsersList(userCreator);
        em.persist(newgroup);
    }

    @Override
    public void joinGroupRequest(String username, long userid, String groupname, String groupType) {
        User user = em.find(User.class, userid);
        Group existgroup = findGroupByName(groupname);

        if (user == null) {
            throw new RuntimeException("User not found with ID: " + userid);
        }

        if (existgroup == null) {
            throw new RuntimeException("Group not found with name: " + groupname);
        }

        if ("public".equalsIgnoreCase(groupType)) {
            existgroup.addToUsersList(user);
            em.merge(existgroup);
        } else if ("private".equalsIgnoreCase(groupType)) {
            existgroup.addToWaitingUsersList(user);
            em.merge(existgroup);
        }
    }

    @Override
    public void addpost(User user, String groupName, Post post) {
        Group group = findGroupByName(groupName);
        if (group == null) {
            throw new RuntimeException("Group not found with name: " + groupName);
        }

        if (!group.getUsers().contains(user)) {
            throw new RuntimeException("User is not a member of this group");
        }

        post.setUser(user);
        post.setGroup(group);
        em.persist(post);

        group.getPosts().add(post);
        em.merge(group);
    }

    @Override
    public void leaveGroup(String username, int userid, String groupname) {
        User user = em.find(User.class, (long) userid);
        Group existgroup = findGroupByName(groupname);

        if (user == null) {
            throw new RuntimeException("User not found with ID: " + userid);
        }

        if (existgroup == null) {
            throw new RuntimeException("Group not found with name: " + groupname);
        }

        // Check if user is the group creator
        if (existgroup.getGroupcreator().equals(username)) {
            throw new RuntimeException("Group creator cannot leave the group. Please transfer ownership or delete the group.");
        }

        existgroup.removeFromUsersList(user);

        // Also remove from admins if the user is an admin
        if (existgroup.getAdmins().contains(user)) {
            existgroup.getAdmins().remove(user);
        }

        em.merge(existgroup);
    }

    @Override
    public Group findGroupByName(String groupname) {
        try {
            TypedQuery<Group> query = em.createQuery(
                    "SELECT g FROM Group g WHERE g.groupname = :groupname", Group.class);
            query.setParameter("groupname", groupname);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public void PromoteUserToBeAdmin(String usercreator, String username, long userid, String groupname) {
        User user = em.find(User.class, userid);
        Group existgroup = findGroupByName(groupname);

        if (user == null) {
            throw new RuntimeException("User not found with ID: " + userid);
        }

        if (existgroup == null) {
            throw new RuntimeException("Group not found with name: " + groupname);
        }

        if (!existgroup.getGroupcreator().equals(usercreator)) {
            throw new RuntimeException("Only the group creator can promote users to admin");
        }

        if (!existgroup.getUsers().contains(user)) {
            throw new RuntimeException("User is not a member of this group");
        }

        existgroup.addToAdminsList(user);
        em.merge(existgroup);
    }

    @Override
    public void removeGroup(String usercreator, String groupname) {
        Group existgroup = findGroupByName(groupname);

        if (existgroup == null) {
            throw new RuntimeException("Group not found with name: " + groupname);
        }

        if (!existgroup.getGroupcreator().equals(usercreator)) {
            throw new RuntimeException("Only the group creator can remove the group");
        }

        em.remove(existgroup);
    }

    @Override
    public void approveJoinRequest(String adminUsername, String username, String groupname) {
        User admin = em.createQuery("SELECT u FROM User u WHERE u.Name = :name", User.class)
                .setParameter("name", adminUsername)
                .getSingleResult();

        User user = em.createQuery("SELECT u FROM User u WHERE u.Name = :name", User.class)
                .setParameter("name", username)
                .getSingleResult();

        Group group = findGroupByName(groupname);

        if (admin == null || user == null || group == null) {
            throw new RuntimeException("Admin, user or group not found");
        }

        if (!group.getAdmins().contains(admin)) {
            throw new RuntimeException("Only admins can approve join requests");
        }

        if (!group.getWaitingUsersList().contains(user)) {
            throw new RuntimeException("User is not in the waiting list");
        }

        group.getWaitingUsersList().remove(user);
        group.addToUsersList(user);

        em.merge(group);
    }
}