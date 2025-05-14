package Service;
import java.util.List;


import Model.Profile;
import Model.User;

@jakarta.ejb.Local
public interface UserService
{
    void registerUser(String email,String password,String UserName,String Role);
    User findUserByEmail(String email);
    User findUserById(long ID);
    User login(String email, String password);
    List<User> GetAllUsers();
    void SendFriendRequest(String currentUserEmail, String friendName);
    void removeFriend(String currentUserEmail, String friendName);
    void acceptFriendRequest(String userEmail,String friendName);
    void RecieveFriendRequest(String username,String friendName);
    User findUserByName(String Name);
    void RejectFriend(String currentUserName, String friendName);
    List<String> viewConnections(String userName);
    void logout(String email) ;
    void UpdateProfile(Profile newProfile,long PID);
    void MakeProfile(Profile profile, Long ID);
    Profile viewProfile(long ID);
    public boolean deleteUser(long targetUserId, User currentUser);
}