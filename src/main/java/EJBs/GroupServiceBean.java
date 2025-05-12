package EJBs;

import Model.Group;
import Model.Post;
import Model.User;
import Service.GroupService;
import Service.NotificationService;
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

    @Override
    public void createGroup(String usercreator,long userid, String groupName, String groupDescription,String groupType) {
        UserServiceBean USB = new UserServiceBean();
        User UserCreat = USB.findUserById(userid);
        if (UserCreat == null) {
            throw new RuntimeException("User not found with ID: " + usercreator);
        }
        Group newgroup = new Group(usercreator,groupName,groupDescription,groupType);
        newgroup.setAdmins(newgroup.addToAdminsList(UserCreat));
        em.persist(newgroup);
    }

    @Override
    public void joinGroupRequest(String username, long userid,String groupname, String groupType){
        NotificationService NS = new NotificationService() ;
        UserServiceBean USB = new UserServiceBean();
        User UserCreat = USB.findUserById(userid);
        Group existgroup= findGroupByName(groupname);
        if (UserCreat == null) {
            throw new RuntimeException("User not found with ID: " + username);
        }
        if (groupType.toLowerCase()=="public"){
            existgroup.addToUsersList(UserCreat);
            NS.sendJoinNotification(username,groupname);
            em.merge(existgroup);
        }
        else if (groupType.toLowerCase()=="private"){
            existgroup.setWaitingUsersList(existgroup.addToWaitingUsersList(UserCreat)); //no handle here

            em.merge(existgroup);
        }
    }
    @Override
    public void addpost(User user, String groupName, Post post) {

    }
    @Override
    public void leaveGroup(String username,int  userid, String groupname) {
        NotificationService NS = new NotificationService() ;
        NS.sendLeaveNotification(username,groupname);
        UserServiceBean USB = new UserServiceBean();
        User UserCreat = USB.findUserById(userid);
        Group existgroup= findGroupByName(groupname);
        existgroup.setUsers(existgroup.removeFromUsersList(UserCreat));
        em.merge(existgroup);
    }


    @Override
    public Group findGroupByName(String groupname){
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
    public void PromoteUserToBeAdmin(String usercreator,String username,long userid,String groupname){
        UserServiceBean USB = new UserServiceBean();
        User UserCreat = USB.findUserById(userid);
        Group existgroup= findGroupByName(groupname);
        if (usercreator != existgroup.getGroupcreator()){
            System.out.println("You are not the group creator");
        }
        else {
        existgroup.setAdmins(existgroup.addToAdminsList(UserCreat));
        existgroup.setUsers(existgroup.removeFromUsersList(UserCreat));
        em.merge(existgroup);
        System.out.println("User promoted to admin");
        }
    }

    @Override
    public void removeGroup(String usercreator,String groupname){
        Group existgroup= findGroupByName(groupname);
        if (usercreator != existgroup.getGroupcreator()){
            System.out.println("You are not the group creator");
        }
        else {
        em.remove(existgroup);
        System.out.println("Group removed");
        }
    }


}
