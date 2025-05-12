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
    public Response RemoveGroups(@PathParam("username") String username,@PathParam("groupname") String groupname)
    {
        try {
            gsb.removeGroup(username,groupname);
            return Response.ok("{\"message\":\"Group deleted successfully\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }

    }

}
