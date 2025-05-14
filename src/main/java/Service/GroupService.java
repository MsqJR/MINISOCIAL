package Service;

import Model.Group;
import Model.Post;
import Model.User;

import java.util.List;

public interface GroupService {
    void createGroup(long userid, String groupName, String groupDescription, String groupType);
    void joinGroupRequest(String username, long userid, String groupname, String groupType);
    void leaveGroup(int userid, String groupname);
    Group findGroupByName(String groupname);
    void PromoteUserToBeAdmin(String usercreator, String username, long userid, String groupname);
    void removeGroup(String usercreator, String groupname);
    List<Group> getAllGroups();
    void addpost(String username, String groupName, String content, String imageUrl, String link);
    void removePostFromGroup(String adminUsername, String groupName, long postId);
    void removeUserFromGroup(String adminUsername, String targetUsername, String groupName);
    void acceptJoinRequest(String username, long userid, String groupname);
    List<User> getUsersInGroup(String groupName);
    List<String> getWaitingUsernamesForGroup(String groupName);
    List<Post>  getGroupPosts(int groupid);

}