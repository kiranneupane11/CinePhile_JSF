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
import java.util.stream.Collectors;

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
    private String trendingSearchTerm;
    private List<TrendingMovieDTO> filteredTrendingMovies;
    
    @PostConstruct
    public void init() {
        this.trendingMovies = movieService.getTrendingMovies();
        this.filteredTrendingMovies = new ArrayList<>(trendingMovies);
    }
    
    public void searchTrendingMovies() {
        String search = (trendingSearchTerm == null) ? "" : trendingSearchTerm.toLowerCase();
        if (search.isEmpty()) {
            filteredTrendingMovies = new ArrayList<>(getTrendingMovies());
        } else {
            filteredTrendingMovies = getTrendingMovies().stream()
                    .filter(movie -> movie.getMovie().getTitle().toLowerCase().contains(search))
                    .collect(Collectors.toList());
        }
        System.out.println("Filtered Trending Movies count: " + filteredTrendingMovies.size());
    }
    
    public List<TrendingMovieDTO> getTrendingMovies() {
        return trendingMovies;
}
        
    public String getTrendingSearchTerm() {
        return trendingSearchTerm;
    }
    public void setTrendingSearchTerm(String trendingSearchTerm) {
        this.trendingSearchTerm = trendingSearchTerm;
    }
    public List<TrendingMovieDTO> getFilteredTrendingMovies() {
        return filteredTrendingMovies;
    }
    public void setFilteredTrendingMovies(List<TrendingMovieDTO> filteredTrendingMovies) {
        this.filteredTrendingMovies = filteredTrendingMovies;
    }

}
