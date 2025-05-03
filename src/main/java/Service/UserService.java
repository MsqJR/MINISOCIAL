package Service;
import java.util.List;


import Model.User;

@jakarta.ejb.Local
public interface UserService
{
    void registerUser(String email,String password);
    User findUserByEmail(String email);
    User findUserById(long ID);
    void login(String email,String password);
    void UpdateProfile(long UID,User newUser);
    List<User> GetAllUsers();
    void SendFriendRequest(String FriendName);
    void removeFriend(String FriendName);
    void AcceptFriendRequest(String FriendName);
    void RecieveFriendRequest(User user);
     User findUserByName(String Name);
    public void RejectFriend(String FriendName);
    public void ViewConnctions(String UserName);
}
