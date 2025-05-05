package Rests;

import Model.*;
import Service.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


import Service.PostService;
import Model.Post;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.POST;

import java.util.ArrayList;
import java.util.List;


import Model.ImageAttachement;
import Model.LinkAttachement;







import EJBs.UserServiceBean;

@Path("/Users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserRest
{
    @EJB
    private UserService usb;

    @POST
    @Path("/login")
    public Response Login(User us) {
        String us_email = us.getEmail();
        String us_pass = us.getPassword();

        try {
            if (us_email == null || us_pass == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Email and password must not be empty.\"}")
                        .build();
            } else {
                usb.login(us_email, us_pass);
                return Response.status(Response.Status.OK)
                        .entity("{\"message\":\"success login\"}")
                        .build();
            }
        } catch (RuntimeException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
    /***********************************************************************************************************/
    @POST
    @Path("/register")
    public Response Register(User us) {
        try {
            if (us.getEmail() == null || us.getPassword() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Email and password must not be empty.\"}")
                        .build();
            }

            usb.registerUser(us.getEmail(), us.getPassword(),us.getName());
            return Response.status(Response.Status.CREATED)
                    .entity("{\"message\":\"User registered successfully.\"}")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }
    /***********************************************************************************************************/
    @GET
    @Path("/getus")
    public Response GetUsers()
    {
        try {
            return Response.ok(usb.GetAllUsers()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Failed to fetch users: " + e.getMessage() + "\"}")
                    .build();
        }
    }
    /***********************************************************************************************************/
    @POST
    @Path("/update/{id}")
    public Response updateUser(@PathParam("id") long id, User updatedUser)
    {
        try {
            usb.UpdateProfile(id, updatedUser);
            return Response.ok("{\"message\":\"User updated successfully\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }

    }
    /************************************************************************************/
    @GET
    @Path("/{username}/friends")
    public Response getFriends(@PathParam("username") String username)
    {
        System.out.println("Fetching friends for user: " + username + " ... ");
        try {
            List<User> friends = usb.viewConnections(username);
            if (friends.isEmpty()) {
                return Response.status(Response.Status.OK)
                        .entity("{\"message\": \"No friends found.\"}")
                        .build();
            }
            return Response.ok(friends).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
    /**********************************************************************************/
    @POST
    @Path("/send/{username}/friends")
    public Response SendFriendRequest(@PathParam("username") String username, User friend)
    {
        if (friend == null || friend.getName() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Friend name must be provided.\"}")
                    .build();
        }

        try {
            usb.SendFriendRequest(username, friend.getName());
            return Response.ok("{\"message\":\"Friend request sent.\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }
    /***********************************************************************************************************/
    @POST
    @Path("/accept/{username}/friends")
    public Response acceptSendRequest(@PathParam("username") String username, User friend)
    {
        if (friend == null || friend.getName() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Friend name must be provided.\"}")
                    .build();
        }

        try {
            usb.acceptFriendRequest(username, friend.getName());
            return Response.ok("{\"message\":\"Friend request accepted.\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    } //

    /***********************************************************************************************************/
    @DELETE
    @Path("/{username}/friends/{friendName}")
    public Response DeleteFriendByPathParam(@PathParam("username") String username, @PathParam("friendName") String friendName)
    {
        if (friendName == null || friendName.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Friend name must be provided.\"}")
                    .build();
        }

        try {
            usb.removeFriend(username, friendName);
            return Response.ok("{\"message\":\"Friend removed successfully.\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    /// /test
    /***********************************************************************************************************/
    @POST
    @Path("/reject/{username}/friends")
    public Response rejectFriendRequest(@PathParam("username") String username, User friend)
    {
        if (friend == null || friend.getName() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Friend name must be provided.\"}")
                    .build();
        }

        try {

            usb.RejectFriend(username, friend.getName());
            return Response.ok("{\"message\":\"Friend request rejected.\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }
    /*********************************************************************************************************************/
    @EJB(beanName = "PostServiceBean")
    private PostService psb;

    @POST
    @Path("/{username}/posts")
    public Response createPost(@PathParam("username") String username, Post post) {
        try {
            if (post == null || post.getContent() == null || post.getContent().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Post content must not be empty.\"}")
                        .build();
            }

            String imageUrl = null;
            String link = null;
            if (post.getMedia() != null) {
                if (post.getMedia() instanceof ImageAttachement) {
                    imageUrl = ((ImageAttachement) post.getMedia()).getImage_url();
                } else if (post.getMedia() instanceof LinkAttachement) {
                    link = ((LinkAttachement) post.getMedia()).getLink();
                }
            }

            psb.createPost(username, post.getContent().trim(), imageUrl, link);
            return Response.status(Response.Status.CREATED)
                    .entity("{\"message\":\"Post created successfully.\"}")
                    .build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }
    /***********************************************************************************************************/
    @GET
    @Path("/hello")
    public String hello()
    {
        return "Hello World";
    }

    @GET
    @Path("/hi")
    public String hi()
    {
        return "hi mark";
    }
/***********************************************************************************************************/

}