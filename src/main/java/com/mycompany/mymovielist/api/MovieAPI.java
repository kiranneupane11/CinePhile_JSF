/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mymovielist.api;
import com.mycompany.mymovielist.model.Movie;
import com.mycompany.mymovielist.service.MovieService;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
/**
 *
 * @author kiran
 */

@Path("/movies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MovieAPI {
    
    @Inject
    private MovieService movieService;
    
    @GET
    public Response getAllMovies(){
        return Response.ok(movieService.getAvailableMovies()).build();
    }
    
    @GET
    @Path("/{id}")
    public Response getMovieById(@PathParam("id") long id){
        return movieService.getMovieById(id)
                .map(movie -> Response.ok(movie))
                .orElse(Response.status(Response.Status.NOT_FOUND))
                .build();
    }
    
    @GET
    @Path("/topRated")
    public Response getTopRatedMovies() {
        return Response.ok(movieService.getTopRatedMovies()).build();
    }
    
    @GET
    @Path("/trending")
    public Response getTrendingMovies(){
        return Response.ok(movieService.getTrendingMovies()).build();
    }
    
    @POST
    @Path("/addMovie")
    public Response addMovie(Movie m){
        movieService.addMovie(m);
        return Response.status(Response.Status.CREATED)
                       .entity("{\"message\":\"Movie added successfully\"}")
                       .build();
    }
    
    @PUT
    @Path("/{id}")
    public Response updateMovie(@PathParam("id") long id, Movie m) {
        if (m.getId() != null && m.getId() != id) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"message\":\"ID Mismatch\"}")
                           .build();
        }
        m.setId(id);
        movieService.updateMovie(m);
            return Response.status(Response.Status.OK)
                           .entity("{\"message\":\"Movie updated successfully\"}")
                           .build();
        }
    
    @DELETE
    @Path("/{id}")
    public Response deleteMovie(@PathParam("id") Long id){
        movieService.deleteMovie(id);
        return Response.status(Response.Status.OK)
                       .entity("{\"message\":\"Movie deleted successfully\"}")
                       .build();
    }
    
}
