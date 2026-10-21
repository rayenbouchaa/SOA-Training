package filtres;

import io.jsonwebtoken.Jwts;

import javax.annotation.Priority;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Key;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    private static final String AUTHENTICATION_SCHEME = "Bearer";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        System.out.println("request filter invoked...");

        // 1. Read the Authorization header
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        System.out.println("authorizationHeader: " + authorizationHeader);

        // 2. It must look like "Bearer <token>"
        if (!isTokenBasedAuthentication(authorizationHeader)) {
            abortWithUnauthorized(requestContext);
            return;
        }

        // 3. Extract the token
        String token = authorizationHeader.substring(AUTHENTICATION_SCHEME.length()).trim();

        // 4. Validate the token (signature + expiration)
        try {
            validateToken(token);
        } catch (Exception e) {
            System.out.println("#### invalid token : " + token);
            abortWithUnauthorized(requestContext);
        }
    }

    private boolean isTokenBasedAuthentication(String authorizationHeader) {
        return authorizationHeader != null
                && authorizationHeader.toLowerCase().startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ");
    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext) {
        requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                .header(HttpHeaders.WWW_AUTHENTICATE, AUTHENTICATION_SCHEME)
                .build());
    }

    // Throws an exception if the token is invalid or expired
    private void validateToken(String token) {
        String keyString = "simplekey";
        Key key = new SecretKeySpec(keyString.getBytes(), 0, keyString.getBytes().length, "HmacSHA512");
        Jwts.parser().setSigningKey(key).parseClaimsJws(token);
        System.out.println("#### valid token : " + token);
    }
}