package Service;
import java.util.List;


import Model.User;

@jakarta.ejb.Local
public interface UserService
{
    void registerUser(String email,String password,String UserName);
    User findUserByEmail(String email);
    User findUserById(long ID);
    public User login(String email, String password);
    void UpdateProfile(long UID,User newUser);
    List<User> GetAllUsers();
    void SendFriendRequest(String currentUserEmail, String friendName);
    public void removeFriend(String currentUserEmail, String friendName);
    void acceptFriendRequest(String userEmail,String friendName);
    void RecieveFriendRequest(User user);
     User findUserByName(String Name);
    void RejectFriend(String currentUserName, String friendName);
    List<User> viewConnections(String userName);

}
