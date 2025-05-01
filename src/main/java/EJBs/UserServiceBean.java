package EJBs;

import java.util.ArrayList;
import java.util.List;

import Model.Friend;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import Model.User;
import Service.UserService;

@Stateless
public class UserServiceBean implements UserService
{
    @PersistenceContext
    private EntityManager em;
    User user = new User();
    Friend friend = new Friend();

    @EJB
    private FriendSeviceBean fservice ;

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
        if (newUser.getBio() != null) {
            existingUser.setBio(newUser.getBio());
        }

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
}
/*****************************************************************************/
@Override
public void RecieveFriendRequest(String FriendName)
{

}
/*****************************************************************************/
@Override
public void removeFriend(String FriendName)
{

}


}