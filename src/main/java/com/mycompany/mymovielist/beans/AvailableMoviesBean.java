/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mymovielist.beans;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped; 
import javax.inject.Inject;
import javax.inject.Named;
import com.mycompany.mymovielist.service.*;
import com.mycompany.mymovielist.model.*;
import java.util.*;
/**
 *
 * @author kiran
 */
@Named("AvailableMoviesBean")
@RequestScoped
public class AvailableMoviesBean {
    
    @Inject
    private MovieService movieService;
    
    private List<Movie> availableMovies;
    
    @PostConstruct
    public void init() {
        availableMovies = movieService.getAvailableMovies();
    }
    
    public List<Movie> getAvailableMovies() {
        return availableMovies;
    }
    
}
