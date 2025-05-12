package Rests;

public class GroupJoinRequest {
    private String username;
    private long userid;
    private String groupname;
    private String groupType;
    private String usercreator;

    // Getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public long getUserid() { return userid; }
    public void setUserid(long userid) { this.userid = userid; }
    public String getGroupname() { return groupname; }
    public void setGroupname(String groupname) { this.groupname = groupname; }
    public String getGroupType() { return groupType; }
    public void setGroupType(String groupType) { this.groupType = groupType; }
    public String getUsercreator() { return usercreator; }
    public void setUsercreator(String usercreator) { this.usercreator = usercreator; }
}