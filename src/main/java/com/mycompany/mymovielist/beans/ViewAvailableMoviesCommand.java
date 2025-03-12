/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mymovielist.beans;
import java.util.*;
import com.mycompany.mymovielist.view.*;
import com.mycompany.mymovielist.model.*;
import com.mycompany.mymovielist.service.*;




/**
 *
 * @author kiran
 */
public class ViewAvailableMoviesCommand implements Command{
    private final ConsoleIO io;
    private final MovieService movieService;
    
    public ViewAvailableMoviesCommand(ConsoleIO io, MovieService movieService) {
        this.io = io;
        this.movieService = movieService;
    }
    
    @Override
    public void execute() {
        List<Movie> movies = movieService.getAvailableMovies();
        if (movies.isEmpty()) {
            io.displayMessage("No movies available.");
        } else {
            for (Movie movie : movies) {
                io.displayMessage(movie.getId() +
                          " | Title: " + movie.getTitle() +
                          " | Year: " + movie.getReleaseYear() +
                          " | Genre: " + movie.getGenre() +
                          " | Rating: " + movie.getRating()+
                          " | Description: " + movie.getDescription());
            }
        }
    }
}
