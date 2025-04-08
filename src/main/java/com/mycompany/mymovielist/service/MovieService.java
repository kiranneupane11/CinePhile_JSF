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

    public List<Movie> getAvailableMovies() {
        return movieRepository.getAll();
    }

    public Optional<Movie> getMovieById(long id) {
        return movieRepository.get(id);
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
