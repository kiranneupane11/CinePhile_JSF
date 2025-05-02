/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mymovielist.service;

import com.mycompany.mymovielist.model.*;
import com.mycompany.mymovielist.repository.*;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Optional;
/**
 *
 * @author kiran
 */
@Named
@ApplicationScoped
public class MovieService {
    
    @Inject
    private MovieRepository movieRepository;
    
    @Inject
    private UserMovieRatingRepository userMovieRatingRepository;
    
    public void addMovie(Movie m){
        movieRepository.add(m);
    }
    
    public void updateMovie(Movie m){
        movieRepository.get(m.getId())
            .orElseThrow(() ->
                new IllegalArgumentException("Movie not found: " + m)
            );
        movieRepository.update(m);
    }

    public List<Movie> getAvailableMovies() {
        return movieRepository.getAll();
    }

    public Optional<Movie> getMovieById(long id) {
        return movieRepository.get(id);
    }
    
    public void deleteMovie(long id){
        Movie movie = movieRepository.get(id)
        .orElseThrow(() ->
            new IllegalArgumentException("Movie not found: " + id)
        );
        movieRepository.remove(movie);
    }
    
    public List<Movie> getMoviesOrderByYearDesc() {
        return movieRepository.findAllOrderByReleaseYearDesc();
    }
    
    public List<Movie> getMoviesOrderByYearAsc() {
        return movieRepository.findAllOrderByReleaseYearAsc();
    }
    
    public List<Movie> getMoviesByGenre(String genre) {
        return movieRepository.findByGenre(genre);
    }
    
    public List<TopRatedMovieDTO> getTopRatedMovies(){
        return userMovieRatingRepository.findTopRatedMovies();
    }
    
    public List<TrendingMovieDTO> getTrendingMovies(){
        return userMovieRatingRepository.getTrendingMovies();
    }
}
