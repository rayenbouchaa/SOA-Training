package webservices;
import filtres.Secured;
import entities.UniteEnseignement;
import metiers.UniteEnseignementBusiness;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
@Path("/ue")
public class UniteRestApi {
    static UniteEnseignementBusiness helper =new UniteEnseignementBusiness();
    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)   // The response will be returned as plain text
    public Response getlistUe() {
        // Build an HTTP 200 (OK) response with the message "Hello World!"
        return Response
                .status(200)                  // HTTP status code 200 = success
                .entity(helper.getListeUE())       // The body of the response
                .build();                     // Builds the final Response object
    }
    @Secured
    @POST
    @Path("/add")
    @Consumes (MediaType.APPLICATION_JSON)
    @Produces (MediaType.TEXT_PLAIN)
    public Response addUe(UniteEnseignement ue)  {
        // Build an HTTP 200 (OK) response with the message "Hello World!"
        if(helper.addUniteEnseignement(ue)){
            return Response.status(201).entity("Created").build();
        }
        else{
            return Response.status(400).entity("Erreur").build();
        }

        // Builds the final Response object
    }
    @GET
    @Path("/{code}")
    @Produces(MediaType.APPLICATION_JSON)   // The response will be returned as plain text
    public Response getUeBYCode(@PathParam("code") int code) {
        // Build an HTTP 200 (OK) response with the message "Hello World!"
        return Response.status(200)                  // HTTP status code 200 = success
                .entity(helper.getUEByCode(code))       // The body of the response
                .build();                     // Builds the final Response object
    }
}
