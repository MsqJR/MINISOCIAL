package Rests;

import Service.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.Context;
import java.util.List;

import EJBs.UserServiceBean;
import Model.User;

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
            usb.registerUser(us.getEmail(), us.getPassword());
            return Response.status(Response.Status.CREATED)
                    .entity("{\"message\":\"User registered successfully.\"}")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"message\":\"Registration failed.\"}"+e.getMessage())
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
                    .entity("{\"message\":\"Failed to fetch users\"}")
                    .build();
        }
    }
/***********************************************************************************************************/
    @POST
    @Path("/{id}")
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
    public Response getFriends(@PathParam("username") String username, @Context SecurityContext securityContext)
    {
        try {
            if (securityContext.getUserPrincipal() == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\": \"User not authenticated.\"}")
                        .build();
            }

            String loggedInUser = securityContext.getUserPrincipal().getName();
            if (!loggedInUser.equals(username) && !securityContext.isUserInRole("admin")) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity("{\"error\": \"Access denied. Only the user or admin can view friends.\"}")
                        .build();
            }

            List<User> friends = usb.viewConnections(username);
            if (friends.isEmpty()) {
                return Response.status(Response.Status.OK)
                        .entity("{\"message\": \"No friends found.\"}")
                        .build();
            }
            return Response.ok(friends).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND)
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
public Response acceptSendRequest(@PathParam("username") String username,User friend)
{
    if (friend == null || friend.getName() == null) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"Friend name must be provided.\"}")
                .build();
    }

    try {
        usb.acceptFriendRequest(username,friend.getName());
        return Response.ok("{\"message\":\"Friend request accepted.\"}").build();
    } catch (Exception e) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
    }
}
/***********************************************************************************************************/
@DELETE
@Path("/delete/{username}/friends")
public Response DeleteFriend(@PathParam("username") String username,User friend)
{
    if (friend == null || friend.getName() == null) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"Friend name must be provided.\"}")
                .build();
    }

    try {
        usb.removeFriend(username,friend.getName());
        return Response.ok("{\"message\":\"Friend request accepted.\"}").build();
    } catch (Exception e) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
    }
}
//need to complete recicve and reject
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