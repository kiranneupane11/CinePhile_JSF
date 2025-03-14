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
@Named
@RequestScoped
public class MovieViewBean {
    
    @Inject
    private MovieService movieService;
    
    private List<Movie> availableMovies;
    
    @PostConstruct
    public void init() {
        try {
        availableMovies = movieService.getAvailableMovies();
    } catch (Exception e) {
        System.err.println("Error in init: " + e.getMessage());
        e.printStackTrace();
        throw e;
    }
    }
    
    public List<Movie> getAvailableMovies() {
        return availableMovies;
    }
    
}
