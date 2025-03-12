/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mymovielist.service;

import com.mycompany.mymovielist.model.Movie;
import com.mycompany.mymovielist.repository.MovieRepository;
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

    public List<Movie> getAvailableMovies() {
        return movieRepository.getAll();
    }

    public Optional<Movie> getMovieById(long id) {
        return movieRepository.get(id);
    }
}
