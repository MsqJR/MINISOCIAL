
package Rests;

import Model.*;
import Service.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


import Service.PostService;
import Model.Post;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.POST;

import java.security.Principal;
import java.util.List;
import Model.ImageAttachement;
import Model.LinkAttachement;
import jakarta.ws.rs.core.SecurityContext;


@Path("/Users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserRest
{
    @EJB
    private UserService usb;

    @Context
    private SecurityContext sc;

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
    /*********************************************************************************/
    @POST
    @Path("/logout")
    public Response user_Logout(User us)
    {
        try {
            usb.logout(us.getEmail());
            return Response.ok("{\"message\":\"User logged out successfully.\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
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

            usb.registerUser(us.getEmail(), us.getPassword(),us.getName(),us.getRole());
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
    /************************************************************************************/
    @GET
    @Path("/{username}/friends")
    public Response getFriends(@PathParam("username") String username)
    {
        System.out.println("Fetching friends for user: " + username + " ... ");
        try {
            List<String> friends = usb.viewConnections(username);
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
        }
        catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }
    /***********************************************************************************************************/
    @GET
    @Path("/recieve/{username}/friends")
    public Response RecieveFriendRequest (@PathParam("username") String username, @QueryParam("friendName") String friendName)
    {
        try {
            if (friendName == null || friendName.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Friend name must be provided.\"}")
                        .build();
            }
            usb.RecieveFriendRequest(username, friendName);
            return Response.ok("{\"message\":\"Friend request received.\"}").build();
        }
        catch (Exception e) {
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
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
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
    @Path("/Posts/feed/{username}")
    public Response getFeed(@PathParam("username") String username) {
        try {
            List<Object> feed = psb.getUserFeed(username);
            if (feed.isEmpty()) {
                return Response.ok("{\"message\": \"No posts found in feed.\"}").build();
            }
            return Response.ok(feed).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Failed to fetch feed: " + e.getMessage() + "\"}")
                    .build();
        }
    }
 /************************************************************************************************************/
 @GET
 @Path("/getProfile/{UID}")
 public Response viewProfile(@PathParam("UID") long UID) {
     Profile profile = usb.viewProfile(UID);
     if (profile == null) {
         return Response.status(Response.Status.NOT_FOUND)
                 .entity("Profile not found for user ID " + UID)
                 .build();
     }
     return Response.ok(profile).build();
 }
 /********************************************************************************************************/
 @POST
 @Path("/make_profile/{UID}")
 public Response MakeProfile(@PathParam("UID") long UID, Profile profile) {
     try {
         usb.MakeProfile(profile, UID);
         return Response.status(Response.Status.CREATED).entity("Profile successfully created").build();
     } catch (RuntimeException e) {
         return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
     } catch (Exception e) {
         return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An error occurred while creating the profile").build();
     }
 }
/***********************************************************************************************************/
@POST
@Path("/update_profile/{uid}")
public Response UpdateProfile(@PathParam("uid") long uid,Profile profile) {
    try {
        usb.UpdateProfile(profile, uid);
        return Response.status(Response.Status.CREATED).entity("Profile updated created").build();
    } catch (RuntimeException e) {
        return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
    } catch (Exception e) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An error occurred while updating the profile").build();
    }
}
/**************************************************************************************************************/
@DELETE
@RolesAllowed("admin")
@Path("/{id}")
public Response deleteUser(@PathParam("id") long id, @HeaderParam("User-Email") String email) {
    if (email == null || email.isEmpty()) {
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity("{\"error\":\"User email header missing\"}").build();
    }

    User currentUser = usb.findUserByEmail(email);
    if (currentUser == null || !"admin".equalsIgnoreCase(currentUser.getRole())) {
        return Response.status(Response.Status.FORBIDDEN)
                .entity("{\"error\":\"Access denied\"}").build();
    }

    try {
        boolean result = usb.deleteUser(id, currentUser);
        if (result) {
            return Response.ok("{\"message\":\"User deleted\"}").build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"User not found\"}").build();
        }
    } catch (SecurityException se) {
        return Response.status(Response.Status.FORBIDDEN)
                .entity("{\"error\":\"" + se.getMessage() + "\"}").build();
    }
}



}


