package Rests;

import Model.Group;
import Service.GroupService;
import Service.PostService;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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
            return Response.ok("{\"message\":\"Join request processed successfully\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }


}