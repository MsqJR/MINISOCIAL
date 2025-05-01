package Rests;

import Service.UserService;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import EJBs.UserServiceBean;
import Model.User;

@Path("/Users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserRest
{
    @EJB
    private UserService usb;

    @GET
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
    public Response updateUser(@PathParam("id") long id, User updatedUser) {
        try {
            usb.UpdateProfile(id, updatedUser);
            return Response.ok("{\"message\":\"User updated successfully\"}").build();
        } catch (Exception e) {
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

}