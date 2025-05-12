package Service;

import Model.Group;
import Model.Post;
import Model.User;

import java.util.List;

public interface GroupService {
    void createGroup(long userid, String groupName, String groupDescription,String groupType);
    void joinGroupRequest(String username, long userid,String group_name, String groupType);
    void addpost(String username, String groupName, String content,String imageUrl,String link);
    void leaveGroup(String username,int  userid, String groupname);
    Group findGroupByName(String groupname);
    void PromoteUserToBeAdmin(String usercreator,String username,long userid,String groupname);
    void removeGroup(String usercreator,String groupname);
    public List <Group> getAllGroups();
}