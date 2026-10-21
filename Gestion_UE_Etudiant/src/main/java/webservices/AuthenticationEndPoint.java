package webservices;

import entities.Credentials;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Path("authentication")
public class AuthenticationEndPoint {

    @Context
    private UriInfo uriInfo;

    // Version 1: data sent as a form (x-www-form-urlencoded)
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response authenticateUser(@FormParam("username") String username,
                                     @FormParam("password") String password) {
        return doAuthenticate(username, password);
    }

    // Version 2: data sent as JSON {"username":"..","password":".."}
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response authenticateUserJson(Credentials cred) {
        return doAuthenticate(cred.getUsername(), cred.getPassword());
    }

    private Response doAuthenticate(String username, String password) {
        System.out.println("username=[" + username + "] password=[" + password + "]");
        try {
            authenticate(username, password);
            String token = issueToken(username);
            return Response.ok(token).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    // Checks the login. Here: one hard-coded user (admin / 1234).
    private void authenticate(String username, String password) {
        System.out.println("Authenticating user...");
        if (!"admin".equals(username) || !"1234".equals(password)) {
            throw new SecurityException("Invalid credentials");
        }
    }

    // Builds the token (JWT)
    private String issueToken(String username) {
        String keyString = "simplekey";
        Key key = new SecretKeySpec(keyString.getBytes(), 0, keyString.getBytes().length, "HmacSHA512");

        String jwtToken = Jwts.builder()
                .setSubject(username)
                .setIssuer(uriInfo.getAbsolutePath().toString())
                .setIssuedAt(new Date())
                .setExpiration(toDate(LocalDateTime.now().plusMinutes(15L)))
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();

        System.out.println("the returned token is : " + jwtToken);
        return jwtToken;
    }

    private Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}