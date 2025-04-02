/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mymovielist.beans;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped; 
import javax.inject.Inject;
import javax.inject.Named;
import com.mycompany.mymovielist.service.*;
import com.mycompany.mymovielist.model.*;
import java.util.*;
import java.io.Serializable;
import java.util.stream.Collectors;
import javax.faces.context.FacesContext;


/**
 *
 * @author kiran
 */
@Named
@ViewScoped
public class MovieViewBean implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private MovieService movieService;
    
    @Inject
    private PlaylistBean playlistBean;
    
    private Movie selectedMovie;
    private List<Movie> availableMovies;
    private List<Movie> filteredMovies;    
    private String searchTerm;
    
    @PostConstruct
    public void init() {
        this.availableMovies = movieService.getAvailableMovies();
        this.filteredMovies = new ArrayList<>(availableMovies);
    }
    
    public void searchMovies() {
        if (searchTerm == null || searchTerm.isEmpty()) {
            filteredMovies = new ArrayList<>(availableMovies);
        } else {
            String lowerSearch = searchTerm.toLowerCase();
            filteredMovies = availableMovies.stream()
                .filter(movie -> movie.getTitle().toLowerCase().contains(lowerSearch))
                .collect(Collectors.toList());
        }
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
    
    public List<Movie> getAvailableMovies() {
        return availableMovies;
    }
    public List<Movie> getFilteredMovies() {
        return filteredMovies;
    }
    public String getSearchTerm() {
        return searchTerm;
    }
    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
        searchMovies();
    }
    
    public int getSelectedMovieRatingAsInt() {
        return selectedMovie != null ? (int) Math.round(selectedMovie.getRating()) : 0;
    }
    
    public String navigateToAddToList() {
        if (selectedMovie != null) {
        return "addtolist.xhtml?faces-redirect=true&amp;movieId=" + selectedMovie.getId();
    }
        return null; 
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
}
