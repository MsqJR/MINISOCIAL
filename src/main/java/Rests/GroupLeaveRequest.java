package Rests;

public class GroupLeaveRequest {
    private long userid;
    private String groupname;

    // Getters and setters
    public long getUserid() { return userid; }
    public void setUserid(long userid) { this.userid = userid; }
    public String getGroupname() { return groupname; }
    public void setGroupname(String groupname) { this.groupname = groupname; }
}