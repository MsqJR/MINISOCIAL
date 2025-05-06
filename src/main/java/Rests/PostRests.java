package Rests;


import EJBs.PostServiceBean;
import Model.ImageAttachement;
import Model.LinkAttachement;
import Model.Post;
import Service.PostService;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/posts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PostRests
{
    @EJB
    private PostService psb;



/**************************************************************************************************/
@DELETE
@Path("/{postID}")
public Response DeletePost(@PathParam("postID") long postID,Post post) {
    try {
        psb.DeletePost(postID, post.getUser().getName());
        return Response.status(Response.Status.OK)
                .entity("{\"message\":\"Post deleted successfully.\"}")
                .build();
    } catch (RuntimeException e) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
    }
}

  @GET
  @Path("/getposts")
  public Response GetPosts()
    {
        try{
            return Response.ok(psb.Getallposts()).build();
    }
        catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Failed to fetch posts: " + e.getMessage() + "\"}")
                    .build();
        }
    }
//TODO need to complete all rests to test all methods of postmanagement
}
