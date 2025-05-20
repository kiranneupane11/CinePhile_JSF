/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mymovielist.api;

/**
 *
 * @author kiran
 */
import com.mycompany.mymovielist.model.Role;
import com.mycompany.mymovielist.service.AuthenticationService;
import com.mycompany.mymovielist.util.JwtUtil;
import com.mycompany.mymovielist.model.User;
import com.mycompany.mymovielist.exception.ApiException;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.time.Duration;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;


@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED })
public class AuthAPI {
    
    @Inject
    AuthenticationService authService;
    
    private static final long ACCESS_TOKEN_EXPIRATION = Duration.ofMinutes(60).getSeconds();
    private static final long REFRESH_TOKEN_EXPIRATION = Duration.ofDays(1).getSeconds();
    
    private static final int ACCESS_TOKEN_MAX_AGE = (int) ACCESS_TOKEN_EXPIRATION;
    private static final int REFRESH_TOKEN_MAX_AGE = (int) REFRESH_TOKEN_EXPIRATION;
    
    private static final String COOKIE_PATH    = "/CinePhile";
    
    public static class TokenResponse {
        public String accessToken;
        private String role;
        private String username;
        
        public TokenResponse(String accessToken, String role, String username) {
                    this.accessToken = accessToken;
                    this.role        = role;
                    this.username    = username;
        }
        public String getAccessToken() { return accessToken; }
        public String getRole()        { return role; }
        public String getUsername()    { return username; }
    }
     
    @POST
    @Path("/signup")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response signup(User newUser) {
        Optional<User> created = authService.signup(newUser);

        if (!created.isPresent()) {
            throw new ApiException(
                Response.Status.BAD_REQUEST.getStatusCode(),
                "Username or email already in use"
            );
        }
        return Response.status(Response.Status.CREATED).build();
    }
    
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@FormParam("usernameOrEmail") String usernameOrEmail,
            @FormParam("password") String password,
            @Context HttpServletResponse servletResp){
       return authService.login(usernameOrEmail, password)
        .map(user -> {
            Role role = user.getRole();
            String accessToken = JwtUtil.generateToken(usernameOrEmail, Role.USER, ACCESS_TOKEN_EXPIRATION);
            String refreshToken = JwtUtil.generateToken(usernameOrEmail, Role.USER, REFRESH_TOKEN_EXPIRATION);
            
            NewCookie accessCookie = new NewCookie("accessToken", accessToken, COOKIE_PATH, null, null, 
                    ACCESS_TOKEN_MAX_AGE, false, true);
            NewCookie refreshCookie = new NewCookie("refreshToken", refreshToken, COOKIE_PATH, null, null, 
                REFRESH_TOKEN_MAX_AGE, false, true);
            
            TokenResponse payload = new TokenResponse(
                    accessToken,
                    role.name(),
                    user.getUsername()
            );
                
            return Response.ok(payload)
                    .cookie(accessCookie)
                    .cookie(refreshCookie)
                    .build();
        })
        .orElseThrow(() -> 
                new ApiException(Response.Status.UNAUTHORIZED.getStatusCode(),
                                 "Invalid credentials")
            );
    }
    
    @POST
    @Path("/refresh")
    public Response refresh(@CookieParam("refreshToken") Cookie refreshTokenCookie) {
        if (refreshTokenCookie == null || !JwtUtil.verifyToken(refreshTokenCookie.getValue())) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        
        String username = JwtUtil.getSubject(refreshTokenCookie.getValue());
        Role role = JwtUtil.getRole(refreshTokenCookie.getValue());
        String newAccessToken = JwtUtil.generateToken(username, role, ACCESS_TOKEN_EXPIRATION);

       NewCookie newAccessCookie = new NewCookie(
            "accessToken", newAccessToken, COOKIE_PATH, null, null,
            ACCESS_TOKEN_MAX_AGE, false, true
        );
       
       TokenResponse payload = new TokenResponse(
                    newAccessToken,
                    role.name(),
                    username
            );
       return Response.ok(payload)
            .cookie(newAccessCookie)
            .build();
    }
    
    @POST
    @Path("/logout")
    public Response logout() {
         NewCookie deleteAccessCookie = new NewCookie(
            "accessToken", "", COOKIE_PATH, null, null,
            0, false, true
        );
        NewCookie deleteRefreshCookie = new NewCookie(
            "refreshToken", "", COOKIE_PATH, null, null,
            0, false, true
        );
        return Response.noContent()
            .cookie(deleteAccessCookie)
            .cookie(deleteRefreshCookie)
            .build();
    }

}
