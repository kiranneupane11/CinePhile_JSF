/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mymovielist.beans;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.*;
import com.mycompany.mymovielist.service.*;
import javax.annotation.PostConstruct;
import com.mycompany.mymovielist.model.*;
import javax.faces.context.FacesContext;
import javax.faces.application.FacesMessage;
import java.util.Collections;
/**
 *
 * @author kiran
 */
@Named
@ViewScoped
public class TrendingBean implements Serializable {
    
    @Inject
    private MovieService movieService;
    
    private List<TrendingMovieDTO> trendingMovies = new ArrayList<>();
    
    @PostConstruct
    public void init() {
        loadTrendingMovies();
    }

    private void loadTrendingMovies(){
        try {
            trendingMovies = movieService.getTrendingMovies();
            if (trendingMovies.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning", "No trending movies available."));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to load trending movies: " + e.getMessage()));
            trendingMovies = Collections.emptyList();
        }
    }
    
        public List<TrendingMovieDTO> getTrendingMovies() {
            return trendingMovies;
    }
    
}
