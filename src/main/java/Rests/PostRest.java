package Rests;

import Model.Post;
import Service.PostService;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/Posts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PostRest
{

    @EJB(beanName = "PostServiceBean")
    private PostService psb;

    @GET
    @Path("/getposts/{username}")
    public Response getPosts(@PathParam("username") String username) {
        try {
            return Response.ok(psb.GetAllPoststhatUserHasPosted(username)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Failed to fetch posts: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    @POST
    @Path("/updateposts/{postID}")
    public Response updatePost(@PathParam("postID") long postID, Post updatedPost)
    {
        try {
            psb.UpdatePost(postID, updatedPost);
            return Response.ok("{\"message\":\"Post updated successfully\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @DELETE
    @Path("/deleteposts/{postID}")
    public Response deletePost(@PathParam("postID") long postID)
    {
        try {
            psb.DeletePost(postID);
            return Response.ok("{\"message\":\"Post deleted successfully\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }
/**********************************************************************************************/

    public static class Request {
        public String username;
        public String text;
    }

    @POST
    @Path("/addComment/{postId}")
    public Response addComment(@PathParam("postId") long postId, Request request) {
        try {
            psb.AddCommentTOPost(postId, request.username, request.text);
            return Response.ok("{\"message\":\"Comment added successfully\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }
/*********************************************************************************************/

@POST
@Path("/addLike/{postId}")
public Response AddLike(@PathParam("postId") long postId,Request request)
{
    try {
        psb.likePost(postId, request.username);
        return Response.ok("{\"message\":\"Liked added successfully\"}").build();
    } catch (Exception e) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
    }


}

}
