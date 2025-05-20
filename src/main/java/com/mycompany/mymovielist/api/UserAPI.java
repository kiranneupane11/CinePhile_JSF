package com.mycompany.mymovielist.api;

import com.mycompany.mymovielist.model.*;
import com.mycompany.mymovielist.service.UserService;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;
import javax.json.*;


@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserAPI {

    @Inject
    private UserService userService;

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllUsers(
        @QueryParam("page") @DefaultValue("0") int page,
        @QueryParam("size") @DefaultValue("5") int size,
        @QueryParam("sort")                    String sort,  
        @QueryParam("filter")                  String filter
    ) {
        int offset = page * size;
        String sortField = null;
        boolean asc       = true;
        
        if (sort != null && sort.contains(",")) {
            String[] parts = sort.split(",", 2);
            sortField = parts[0];
            asc       = "asc".equalsIgnoreCase(parts[1]);
        }
        
        // parse filter JSON into a Map<String,String>
        Map<String,String> filters = new HashMap<>();
        if (filter != null && !filter.isEmpty()) {
            JsonObject obj = Json.createReader(new java.io.StringReader(filter)).readObject();
            for (String key : obj.keySet()) {
                JsonValue node = obj.get(key);
                if (node.getValueType() == JsonValue.ValueType.OBJECT) {
                    JsonObject nested = obj.getJsonObject(key);
                    // extract the "value" field from that nested object
                    if (nested.containsKey("value") && !nested.isNull("value")) {
                        filters.put(key, nested.getString("value", ""));
                    }
                }
            }
        }
        
       // fetch data
        List<User>  pageItems    = userService.getUsersPage(offset, size, sortField, asc, filters);
        long        totalFiltered = userService.countUsers(filters);

        return Response.ok(pageItems)
                       .header("X-Total-Count", totalFiltered)
                       .build();
    }

    @POST
    @Path("/{id}/delete")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response removeUser(@PathParam("id") Long id) {
        User user = userService.getUsersList().stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
        if (user != null) {
            userService.removeUser(user);
            return Response.ok().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Path("/{id}/update")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(
        @PathParam("id") Long id,
        @FormParam("username") @DefaultValue("") String username,
        @FormParam("email")    @DefaultValue("") String email,
        @FormParam("role")     @DefaultValue("USER") Role role
    ) {
        User existingUser = userService.getUsersList().stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
        if (existingUser != null) {
            if (!username.isEmpty()) existingUser.setUsername(username);
            if (!email.   isEmpty()) existingUser.setEmail(email);
            existingUser.setRole(role);
            userService.updateUser(existingUser);
            return Response.ok(existingUser).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}