package EJBs;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    @Override
    public void registerUser(String email, String password,String UserName) {
        if (findUserByEmail(email) != null) {
            System.out.println("This email already exists");
        } else {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setPassword(password);
            newUser.setRole("User");// Default role


            newUser.setName(UserName);

            em.persist(newUser);
        }
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
        User user = findUserByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        } else {
            throw new RuntimeException("Invalid email or password.");
        }
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

        em.merge(existingUser);
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
    public void RecieveFriendRequest(User user)
    {
        User friend = findUserByName(user.getName());
        if (friend == null) {
            throw new RuntimeException("No user found with name: " + user.getName());
        }
        friend.getFriendRequests().add(user);
        em.merge(friend);
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
    public void RejectFriend(String currentUserEmail, String friendName) {
        User user = findUserByEmail(currentUserEmail);
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
    public List<User> viewConnections(String userName) {
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