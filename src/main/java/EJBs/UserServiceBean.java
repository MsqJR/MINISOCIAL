package EJBs;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import Model.User;
import Service.UserService;
import jdk.jfr.Frequency;

@Stateless
public class UserServiceBean implements UserService
{
    @PersistenceContext
    private EntityManager em;
    User user = new User();


    @Override
    public void registerUser(String email, String password) {
        if (findUserByEmail(email) != null) {
            System.out.print("This email already exists");
        } else {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setPassword(password);
            newUser.setRole("User"); // Default role for new users
            em.persist(newUser);
        }
    }
/******************************************************************************************/
@Override
public User findUserByEmail(String email) {
    try {
        TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u WHERE u.email = :email", User.class);
        query.setParameter("email", email);
        return query.getSingleResult();
    } catch (NoResultException e) {
        return null;
    }
}
/*******************************************************************************************/
    @Override
    public void login(String email, String password) {
        user = findUserByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            System.out.print("Successfully logged in");
        } else {
            throw new RuntimeException("Invalid email or password.");
        }
    }
/*************************************************************************************/
    @Override
    public User findUserById(long ID) {
        try {
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u WHERE u.ID = :ID", User.class);
            query.setParameter("ID", ID);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
 /*********************************************************************************************/
 @Override
 public User findUserByName(String Name) {
     try {
         TypedQuery<User> query = em.createQuery(
                 "SELECT u FROM User u WHERE u.Name = :Name", User.class);
         query.setParameter("Name", Name);
         return query.getSingleResult();
     } catch (NoResultException e) {
         return null;
     }
 }
/***********************************************************************************************/
    @Override
    public void UpdateProfile(long UID, User newUser) {
        User existingUser = em.find(User.class, UID);
        if (existingUser == null) {
            throw new RuntimeException("User not found with ID: " + UID);
        }

        if (newUser.getName() != null) {
            existingUser.setName(newUser.getName());
        }
        if (newUser.getPassword() != null) {
            existingUser.setPassword(newUser.getPassword());
        }
        if (newUser.getEmail() != null) {
            existingUser.setEmail(newUser.getEmail());
        }
        /*NeedTOUpdateBio*/
        //upadate bio

        em.merge(existingUser);
    }
/************************************************************************/
    @Override
    public List<User> GetAllUsers() {
        try {
            TypedQuery<User> q = em.createQuery("SELECT us FROM User us", User.class);
            return q.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<>();
        }
    }
 /*****************************************************************************/
@Override
public void SendFriendRequest(String FriendName)
{
    User Friend = findUserByName(FriendName);
    if(Friend==null)
        throw new RuntimeException("User not found");

    if (Friend.getFriendRequests().contains(user)) {
        System.out.println("Friend request already sent");
        return;
    }
        System.out.println("successfully sent request");
        Friend.getFriendRequests().add(user);
        em.merge(Friend);
        // RecieveFriendRequest(user);
}
/*********************************************************************************/
@Override
public void RecieveFriendRequest(User Sender)
{
   user.setFriendRequests(Sender);
   em.merge(Sender);
}
/*****************************************************************************/
@Override
public void AcceptFriendRequest(String FriendName)
{
    User Friend = findUserByName(FriendName);
    if (Friend == null) {
        throw new RuntimeException("User not found");
    }
    if (user.getFriendRequests().contains(Friend)) {
        Friend.getFriends().add(user);
        user.getFriends().add(Friend);

        user.getFriendRequests().remove(Friend);

        em.merge(Friend);
        em.merge(user);
    } else {
        System.out.println("No request found");
    }
}
/*****************************************************************************/
@Override
public void removeFriend(String FriendName)
{
    User Friend = findUserByName(FriendName);
    if (Friend == null) {
        throw new RuntimeException("User not found");
    }
    if (user.getFriends().contains(Friend)) {
        user.getFriends().remove(Friend);
        Friend.getFriends().remove(user);
        em.merge(Friend);
        em.merge(user);
    }
}
/*****************************************************************************************/
@Override
 public void RejectFriend(String FriendName)
 {
     User Friend = findUserByName(FriendName);
     if (Friend == null) {
         throw new RuntimeException("User not found");
     }
     if (user.getFriendRequests().contains(Friend)) {
         user.getFriendRequests().remove(Friend);
         em.merge(user);
     }
    }
 /************************************************************************************/
//need to implement (Users can view all their friends and their profiles.)
 @Override
 public List<User> viewConnections(String userName)
 {
     User currentUser = findUserByName(userName);
     if (currentUser == null) {
         throw new RuntimeException("User not found");
     }
     Set<User> friends = currentUser.getFriends();
     if (friends == null || friends.isEmpty()) {
         System.out.println("No friends found for user: " + userName);
         return new ArrayList<>();
     }
     System.out.println("Successfully retrieved " + friends.size() + " friends for user: " + userName);
     return new ArrayList<>(friends);

 }

}