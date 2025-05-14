package EJBs;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


import Messaging.NotificationService;
import Model.Profile;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import Model.User;


import Service.UserService;

@Stateless
public class UserServiceBean implements UserService {

    @PersistenceContext
    private EntityManager em;

    @EJB(beanName = "NotificationServiceBean" )
    private NotificationService nss;

    private User currentUser;
    private Profile currentProfile;


    private List<String> loggedInUsers = new ArrayList<>();


    @Override
    public void registerUser(String email, String password,String UserName,String Role) {
        if (findUserByEmail(email) != null) {
            throw new RuntimeException("This email already exists");
        }
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setPassword(password);
            if(Role.equals("admin")){
                newUser.setRole("admin");
            }
           else
           {
              newUser.setRole("user");
           }

           newUser.setName(UserName);

            em.persist(newUser);
        }


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
    @Override
    public User login(String email, String password) {
        if (loggedInUsers.contains(email)) {
            throw new RuntimeException("User already logged in.");
        }
        User user = findUserByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            loggedInUsers.add(email);
            return user;
        } else {
            throw new RuntimeException("Invalid email or password.");
        }
    }

    @Override
    public void logout(String email) {
        loggedInUsers.remove(email);
    }

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

    @Override
    public List<User> GetAllUsers() {
        try {
            TypedQuery<User> q = em.createQuery("SELECT us FROM User us", User.class);
            return q.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public void SendFriendRequest(String UserName, String friendName) {
        User sender = findUserByName(UserName);
        User recipient = findUserByName(friendName);

        if (sender == null || recipient == null) {
            throw new RuntimeException("User not found");
        }

        if (recipient.getFriendRequests().contains(sender)) {
            System.out.println("Friend request already sent");
            return;
        }

        recipient.getFriendRequests().add(sender);
        em.merge(recipient);
        System.out.println("Successfully sent request");

    }

    @Override
    public void acceptFriendRequest(String username, String friendName) {

        User user = findUserByName(username);
        if (user == null) {
            user = findUserByEmail(username);
        }

        User friend = findUserByName(friendName);

        if (user == null) {
            throw new RuntimeException("User not found: " + username);
        }

        if (friend == null) {
            throw new RuntimeException("Friend not found: " + friendName);
        }

        if (user.getFriendRequests().contains(friend)) {
            user.getFriendRequests().remove(friend);
            user.getFriends().add(friend);
            friend.getFriends().add(user);

            em.merge(user);
            em.merge(friend);
            System.out.println("Friend request accepted from " + friend.getName() + " by " + user.getName());
        } else {
            throw new RuntimeException("No friend request found from " + friendName);
        }
    }

    @Override
    public void RecieveFriendRequest(String username,String friendname)
    {
        User user = findUserByName(username);
        if (user == null) {
            user = findUserByEmail(username);
        }
        User friend = findUserByName(friendname);
        if (user == null) {
            throw new RuntimeException("User not found: " + username);
        }
        if (friend == null) {
            throw new RuntimeException("Friend not found: " + friendname);
        }
        if (friend.getFriendRequests().contains(user))
        {

            System.out.println("Friend request received from " + friend.getName() + " by " + user.getName());
            nss.sendFriendRequestNotification(user.getName(),friend.getName());
        }
   else
        {
            System.out.println("No friend request found from " + friendname);
        }
    }

    @Override
    public void removeFriend(String username, String friendName) {
        User user = findUserByName(username);
        if (user == null) {
            user = findUserByEmail(username);
        }

        User friend = findUserByName(friendName);

        if (user == null) {
            throw new RuntimeException("User not found: " + username);
        }

        if (friend == null) {
            throw new RuntimeException("Friend not found: " + friendName);
        }

        if (user.getFriends().contains(friend)) {
            user.getFriends().remove(friend);
            friend.getFriends().remove(user);
            em.merge(user);
            em.merge(friend);
            System.out.println("Friend removed: " + friend.getName() + " from " + user.getName());
        } else {
            throw new RuntimeException("Users are not friends: " + user.getName() + " and " + friendName);
        }
    }

    @Override
    public void RejectFriend(String currentUserName, String friendName) {
        User user = findUserByName(currentUserName);
        User friend = findUserByName(friendName);

        if (user == null || friend == null) {
            throw new RuntimeException("User not found");
        }

        if (user.getFriendRequests().contains(friend)) {
            user.getFriendRequests().remove(friend);
            em.merge(user);
        }
    }

    @Override
    public List<String> viewConnections(String userName) {
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

        // Return only the names of friends
        return friends.stream()
                .map(User::getName)
                .collect(Collectors.toList());
    }

    @Override
    public void UpdateProfile(Profile newProfile, long PID)
    {
        Profile existingProfile = em.find(Profile.class, PID);

        if (existingProfile == null) {
            throw new RuntimeException("Profile with ID " + PID + " not found");
        }

        User user = existingProfile.getUser();
        if (user == null || !loggedInUsers.contains(user.getEmail())) {
            throw new RuntimeException("User not logged in or user not found");
        }


        existingProfile.setProfile_name(newProfile.getProfile_name());
        existingProfile.setBio(newProfile.getBio());


         existingProfile.setProfile_email(newProfile.getProfile_email());
         existingProfile.setProfile_password(newProfile.getProfile_password());
        existingProfile.setProfile_role(newProfile.getProfile_role());

        em.merge(existingProfile);
        em.flush();

        System.out.println("Profile with ID " + PID + " updated successfully");
    }



    @Override
    public void MakeProfile(Profile profile,Long ID )
    {
        currentUser = findUserById(ID);
        if (currentUser == null)
        {
            throw new RuntimeException("user not found");
        }
        if (!loggedInUsers.contains(currentUser.getEmail()))
        {
            throw new RuntimeException("user not logged in");
        }


        else {
            profile.setProfile_password(currentUser.getPassword());
            profile.setProfile_email(currentUser.getEmail());
            profile.setUser(currentUser);
            profile.setProfile_role(currentUser.getRole());
            profile.setBio(profile.getBio());
            profile.setProfile_name(profile.getProfile_name());

            currentUser.setUser_profile(profile);


            em.persist(profile);
            em.flush();
            System.out.println("Successfully created profile");
        }
    }
    @Override
    public Profile viewProfile(long UID) {
        try {
            TypedQuery<Profile> q = em.createQuery("SELECT p FROM Profile p WHERE p.user.ID = :uid", Profile.class);
            q.setParameter("uid", UID);
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

}