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
import java.time.Year;
import java.util.stream.IntStream;
import javax.faces.context.FacesContext;
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;

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
    
    private Movie selectedMovie;
    private List<Movie> selectedMovies;
    private List<Movie> availableMovies;
    private List<Movie> filteredMovies;
    private String searchTerm;
    private Integer filterReleaseYear;
    private String filterGenre;
    private List<Year> releaseYears;
    private Movie movieToDelete;
    
    @PostConstruct
    public void init() {
        this.availableMovies = movieService.getAvailableMovies();
        this.filteredMovies = new ArrayList<>(availableMovies);
        int currentYear = Year.now().getValue();
        releaseYears = IntStream.rangeClosed(1900, currentYear)
                                .mapToObj(Year::of)
                                .collect(Collectors.toList());
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
    
    public void filterByYearDescending() {
        filteredMovies = movieService.getMoviesOrderByYearDesc();
    }
    
    public void filterByYearAscending() {
        filteredMovies = movieService.getMoviesOrderByYearAsc();
    }
    
    public void filterByGenre() {
        if (filterGenre != null && !filterGenre.trim().isEmpty()) {
            filteredMovies = movieService.getMoviesByGenre(filterGenre);
        } else {
            filteredMovies = new ArrayList<>(availableMovies);
        }
    }
    
    public Integer getFilterReleaseYear() {
        return filterReleaseYear;
    }

    public void setFilterReleaseYear(Integer filterReleaseYear) {
        this.filterReleaseYear = filterReleaseYear;
    }
    
    public String getFilterGenre() {
        return filterGenre;
    }

    public void setFilterGenre(String filterGenre) {
        this.filterGenre = filterGenre;
    }
    
    public void selectMovie(Movie movie) {
        selectedMovie = movie;
    }
    
    public Movie getSelectedMovie() {
        return selectedMovie;
    }
    public void setSelectedMovie(Movie m) {
        this.selectedMovie = m;
    }
    
    public List<Movie> getSelectedMovies() {
        return selectedMovies;
    }
    public void setSelectedMovies(List<Movie> selectedMovies) {
        this.selectedMovies = selectedMovies;
    }
    
    public List<Movie> getAvailableMovies() {
        return availableMovies;
    }
    public List<Movie> getFilteredMovies() {
        return filteredMovies;
    }
    public List<Year> getReleaseYears() {
        return releaseYears;
    }
    public String getSearchTerm() {
        return searchTerm;
    }
    
    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
        if (availableMovies != null) {
            searchMovies();
        }
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
    
    public void openNew() {
        this.selectedMovie = new Movie();
    }
    
    public void onEditMovie(Movie movie) {
        this.selectedMovie = movie;
    }
    
    public void saveMovie(ActionEvent event) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        boolean isNew = (selectedMovie.getId() == null);

        try {
            if (isNew) {
                movieService.addMovie(selectedMovie);
                ctx.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                                     "Created",
                                     "Movie \"" + selectedMovie.getTitle() + "\" has been added."));
            } else {
                movieService.updateMovie(selectedMovie);
                ctx.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                                     "Updated",
                                     "Movie \"" + selectedMovie.getTitle() + "\" has been saved."));
            }

            // Refresh the lists
            this.availableMovies = movieService.getAvailableMovies();
            this.filteredMovies  = new ArrayList<>(availableMovies);

            this.selectedMovie = null;

        } catch (Exception e) {
            String summary = isNew ? "Create failed" : "Update failed";
            ctx.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                 summary,
                                 e.getMessage()));
        }
    }

    
    public void prepareDelete(Movie m) {
        this.movieToDelete = m;
    }

    public void confirmDelete() {
        if (movieToDelete != null) {
            onDeleteMovie(movieToDelete);
            movieToDelete = null;
        }
    }
    
    public void onDeleteMovie(Movie movie) {
        try {
            movieService.deleteMovie(movie.getId());

            availableMovies = movieService.getAvailableMovies();
            filteredMovies  = new ArrayList<>(availableMovies);

            FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                                 "Deleted",
                                 "Movie \"" + movie.getTitle() + "\" has been removed."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                 "Delete Failed",
                                 e.getMessage()));
        }
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
