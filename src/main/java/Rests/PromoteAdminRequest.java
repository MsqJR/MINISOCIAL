package Rests;

public class PromoteAdminRequest {
    private String usercreator;
    private String username;
    private long userid;
    private String groupname;

    // Getters and setters
    public String getUsercreator() { return usercreator; }
    public void setUsercreator(String usercreator) { this.usercreator = usercreator; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public long getUserid() { return userid; }
    public void setUserid(long userid) { this.userid = userid; }
    public String getGroupname() { return groupname; }
    public void setGroupname(String groupname) { this.groupname = groupname; }
}