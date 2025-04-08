/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mymovielist.beans;

/**
 *
 * @author kiran
 */

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import com.mycompany.mymovielist.service.*;
import javax.annotation.PostConstruct;
import com.mycompany.mymovielist.model.*;
import java.util.AbstractMap;
import javax.faces.context.FacesContext;
import javax.faces.application.FacesMessage;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;


@Named
@ViewScoped
public class TopRatedBean implements Serializable {
    @Inject
    private MovieService movieService;
    
    private List<TopRatedMovieDTO> topRatedMovies;
    private String topRatedSearchTerm;
    private List<TopRatedMovieDTO> filteredTopRatedMovies;
    private Movie selectedMovie;


    @PostConstruct
    public void init() {
        this.topRatedMovies = movieService.getTopRatedMovies();
        this.filteredTopRatedMovies = new ArrayList<>(getTopRatedMovies());
    }
    
    public void searchTopRatedMovies() {
        String search = (topRatedSearchTerm == null) ? "" : topRatedSearchTerm.toLowerCase();
        if (search.isEmpty()) {
            filteredTopRatedMovies = new ArrayList<>(getTopRatedMovies());
        } else {
            filteredTopRatedMovies = getTopRatedMovies().stream()
                    .filter(movie -> movie.getMovie().getTitle().toLowerCase().contains(search))
                    .collect(Collectors.toList());
        }
        System.out.println("Filtered Top Rated Movies count: " + filteredTopRatedMovies.size());
    }
    
    public List<Map.Entry<String, Object>> getMovieDetails() {
        if (selectedMovie == null) return Collections.emptyList();
        List<Map.Entry<String, Object>> details = new ArrayList<>();
        details.add(new AbstractMap.SimpleEntry<>("Title", selectedMovie.getTitle()));
        details.add(new AbstractMap.SimpleEntry<>("Release Year", selectedMovie.getReleaseYear()));
        details.add(new AbstractMap.SimpleEntry<>("Genre", selectedMovie.getGenre()));
        details.add(new AbstractMap.SimpleEntry<>("Rating", selectedMovie.getRating()));
        details.add(new AbstractMap.SimpleEntry<>("Description", selectedMovie.getDescription()));
        return details;
    }

    public List<TopRatedMovieDTO> getTopRatedMovies() {
        return movieService.getTopRatedMovies();
    }
    
    public String getTopRatedSearchTerm() {
        return topRatedSearchTerm;
    }
    public void setTopRatedSearchTerm(String topRatedSearchTerm) {
        this.topRatedSearchTerm = topRatedSearchTerm;
    }
    public List<TopRatedMovieDTO> getFilteredTopRatedMovies() {
        return filteredTopRatedMovies;
    }
    public void setFilteredTopRatedMovies(List<TopRatedMovieDTO> filteredTopRatedMovies) {
        this.filteredTopRatedMovies = filteredTopRatedMovies;
    }
    
    public void selectMovie(Movie movie) {
        selectedMovie = movie;
    }
    public Movie getSelectedMovie() {
        return selectedMovie;
    }

    public void setSelectedMovie(Movie selectedMovie) {
        this.selectedMovie = selectedMovie;
    }
    
    public String navigateToAddToList() {
        if (selectedMovie != null) {
        return "addtolist.xhtml?faces-redirect=true&amp;movieId=" + selectedMovie.getId();
    }
        return null; 
    }
    
     public int getSelectedMovieRatingAsInt() {
        return selectedMovie != null ? (int) Math.round(selectedMovie.getRating()) : 0;
    }

}
