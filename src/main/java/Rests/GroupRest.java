package Rests;

import Model.Group;
import Model.User;
import Service.GroupService;
import Service.PostService;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/Group")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GroupRest {

    @EJB(beanName = "GroupServiceBean")
    private GroupService gsb;

    @EJB(beanName = "PostServiceBean")
    private PostService psb;

    @POST
    @Path("/CreateGroup/{id}")
    public Response CreateGroup(@PathParam("id") long id, Group group) {
        try {
            gsb.createGroup(id, group.getGroupName(), group.getGroupDescription(), group.getGroupType());
            return Response.ok("{\"message\":\"Group created successfully\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @GET
    @Path("/getgroups")
    public Response GetGroups() {
        try {
            return Response.ok(gsb.getAllGroups()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Failed to fetch groups: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    @DELETE
    @Path("/deletegroup/{username}/{groupname}")
    public Response RemoveGroups(@PathParam("username") String username, @PathParam("groupname") String groupname) {
        try {
            gsb.removeGroup(username, groupname);
            return Response.ok("{\"message\":\"Group deleted successfully\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @GET
    @Path("/getgroup/{groupname}")
    public Response getGroupByName(@PathParam("groupname") String groupname) {
        try {
            Group group = gsb.findGroupByName(groupname);
            if (group == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Group not found with name: " + groupname + "\"}")
                        .build();
            }
            return Response.ok(group).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Failed to fetch group: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    @POST
    @Path("/promoteToAdmin")
    public Response promoteUserToAdmin(GroupJoinRequest request) {
        try {
            if (request.getUsercreator() == null || request.getUsercreator().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"Creator username is required\"}")
                        .build();
            }
            if (request.getUsername() == null || request.getUsername().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"Username is required\"}")
                        .build();
            }
            if (request.getGroupname() == null || request.getGroupname().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"Group name is required\"}")
                        .build();
            }
            gsb.PromoteUserToBeAdmin(request.getUsercreator(), request.getUsername(), request.getUserid(), request.getGroupname());
            return Response.ok("{\"message\":\"User promoted to admin successfully\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @POST
    @Path("/joinGroup")
    public Response joinGroupRequest(GroupJoinRequest request) {
        try {
            if (request.getUsername() == null || request.getUsername().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"Username is required\"}")
                        .build();
            }
            if (request.getGroupname() == null || request.getGroupname().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"Group name is required\"}")
                        .build();
            }
            if (!"public".equalsIgnoreCase(request.getGroupType()) && !"private".equalsIgnoreCase(request.getGroupType())) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"Group type must be 'public' or 'private'\"}")
                        .build();
            }

            gsb.joinGroupRequest(request.getUsername(), request.getUserid(), request.getGroupname(), request.getGroupType());
            if ("public".equalsIgnoreCase(request.getGroupType())) {
                return Response.ok("{\"message\":\"Successfully joined public group\"}").build();
            } else {
                return Response.ok("{\"message\":\"Join request sent to private group\"}").build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @POST
    @Path("/leaveGroup")
    public Response leaveGroup(GroupLeaveRequest request) {
        try {
            if (request.getUsername() == null || request.getUsername().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"Username is required\"}")
                        .build();
            }
            if (request.getGroupname() == null || request.getGroupname().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"Group name is required\"}")
                        .build();
            }
            gsb.leaveGroup(request.getUsername(), (int) request.getUserid(), request.getGroupname());
            return Response.ok("{\"message\":\"User left group successfully\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    public static class PostRequest {
        public String username;
        public String groupName;
        public String content;
        public String imageUrl;
        public String link;
    }

    @POST
    @Path("/addPost")
    public Response addPostToGroup(PostRequest request) {
        try {
            if (request.username == null || request.username.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"Username is required\"}")
                        .build();
            }
            if (request.groupName == null || request.groupName.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"Group name is required\"}")
                        .build();
            }
            if (request.content == null || request.content.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"Post content cannot be empty\"}")
                        .build();
            }

            gsb.addpost(request.username, request.groupName, request.content, request.imageUrl, request.link);
            return Response.ok("{\"message\":\"Post added to group successfully\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    public static class RemovePostRequest {
        public String adminUsername;
        public String groupName;
        public long postId;
    }

    public static class RemoveUserRequest {
        public String adminUsername;
        public String targetUsername;
        public String groupName;
    }

    @POST
    @Path("/removePost")
    public Response removePostFromGroup(RemovePostRequest request) {
        try {
            if (request.adminUsername == null || request.adminUsername.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"Admin username is required\"}")
                        .build();
            }
            if (request.groupName == null || request.groupName.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"Group name is required\"}")
                        .build();
            }
            if (request.postId <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"Valid post ID is required\"}")
                        .build();
            }

            gsb.removePostFromGroup(request.adminUsername, request.groupName, request.postId);
            return Response.ok("{\"message\":\"Post removed from group successfully\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @POST
    @Path("/removeUser")
    public Response removeUserFromGroup(RemoveUserRequest request) {
        try {
            if (request.adminUsername == null || request.adminUsername.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"Admin username is required\"}")
                        .build();
            }
            if (request.targetUsername == null || request.targetUsername.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"Target username is required\"}")
                        .build();
            }
            if (request.groupName == null || request.groupName.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"Group name is required\"}")
                        .build();
            }

            gsb.removeUserFromGroup(request.adminUsername, request.targetUsername, request.groupName);
            return Response.ok("{\"message\":\"User removed from group successfully\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @POST
    @Path("/acceptJoinRequest")
    public Response acceptJoinRequest(GroupJoinRequest request) {
        try {
            if (request.getUsername() == null || request.getUsername().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"Username is required\"}")
                        .build();
            }
            if (request.getGroupname() == null || request.getGroupname().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"Group name is required\"}")
                        .build();
            }

            gsb.acceptJoinRequest(request.getUsername(), request.getUserid(), request.getGroupname());
            return Response.ok("{\"message\":\"Join request accepted successfully\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }


    @GET
    @Path("/get_group_users/{groupname}")
    public Response getGroupUsers(@PathParam("groupname") String groupname) {
        try {
            List<User> users = gsb.getUsersInGroup(groupname);
            if (users != null && !users.isEmpty()) {
                return Response.ok(users).build();
            } else {
                return Response.status(Response.Status.NO_CONTENT)
                        .entity("{\"message\":\"No users found in this group.\"}")
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @GET
    @Path("/get_waiting_users/{groupname}")
    public Response getWaitingUsers(@PathParam("groupname") String groupname) {
        try {
            List<String> waitingUsernames = gsb.getWaitingUsernamesForGroup(groupname);

            Map<String, Object> response = new HashMap<>();
            response.put("groupName", groupname);
            response.put("waitingUsers", waitingUsernames);

            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    public static class GroupJoinRequest {
        private String username;
        private long userid;
        private String groupname;
        private String groupType;
        private String usercreator;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public long getUserid() {
            return userid;
        }

        public void setUserid(long userid) {
            this.userid = userid;
        }

        public String getGroupname() {
            return groupname;
        }

        public void setGroupname(String groupname) {
            this.groupname = groupname;
        }

        public String getGroupType() {
            return groupType;
        }

        public void setGroupType(String groupType) {
            this.groupType = groupType;
        }

        public String getUsercreator() {
            return usercreator;
        }

        public void setUsercreator(String usercreator) {
            this.usercreator = usercreator;
        }
    }

    public static class GroupLeaveRequest {
        private String username;
        private long userid;
        private String groupname;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public long getUserid() {
            return userid;
        }

        public void setUserid(long userid) {
            this.userid = userid;
        }

        public String getGroupname() {
            return groupname;
        }

        public void setGroupname(String groupname) {
            this.groupname = groupname;
        }
    }
}
