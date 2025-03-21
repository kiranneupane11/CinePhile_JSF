package com.mycompany.mymovielist.beans;

import com.mycompany.mymovielist.model.*;
import com.mycompany.mymovielist.service.*;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.inject.Inject;
import javax.faces.context.FacesContext;
import javax.faces.application.FacesMessage;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class PlaylistBean implements Serializable {
    @Inject
    private PlaylistService playlistService;

    @Inject
    private AuthenticationBean authenticationBean;

    private User loggedInUser;
    private List<UserPlaylist> userPlaylist;
    private double rating;
    private UserMovieRating.Status status;
    private String listName;
    private UserPlaylist selectedPlaylist;
    private List<UserMovieRatingDTO> selectedPlaylistMovies;
    private Movie selectedMovie; 

    @PostConstruct
    public void init() {
        this.loggedInUser = authenticationBean.getLoggedInUser();
        if (loggedInUser != null) {
            userPlaylist = playlistService.getUserLists(loggedInUser);
            String[] defaultLists = {"Watching", "Watched", "Plan to Watch", "Dropped"};
            for (String list : defaultLists) {
                if (!playlistService.listExists(loggedInUser, list)) {
                    playlistService.createList(loggedInUser, list);
                }
            }
            userPlaylist = playlistService.getUserLists(loggedInUser);
        }
    }

    public void addToList() {
        if (loggedInUser == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "You must be logged in to add movies."));
            return;
        }
        if (selectedMovie == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No movie selected."));
            return;
        }
        UserMovieRating userMovieRating = new UserMovieRating(selectedMovie, rating, status, loggedInUser);
        listName = status.toString().replace("_", " "); 
        if (playlistService.addMovieToList(loggedInUser, userMovieRating, listName)) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Movie added successfully!"));
            userPlaylist = playlistService.getUserLists(loggedInUser); 
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to add movie."));
        }
    }

    public void selectPlaylist(String playlistName) {
        userPlaylist = playlistService.getUserLists(loggedInUser);
        selectedPlaylist = userPlaylist.stream()
            .filter(p -> p.getListName().equals(playlistName))
            .findFirst()
            .orElse(null);
        if (selectedPlaylist != null) {
            selectedPlaylistMovies = playlistService.viewList(selectedPlaylist, loggedInUser);
        } else {
            selectedPlaylistMovies = null;
        }
    }

    public void deleteList(String playlistName) {
        userPlaylist = playlistService.getUserLists(loggedInUser);
        UserPlaylist playlistToDelete = userPlaylist.stream()
            .filter(p -> p.getListName().equals(playlistName))
            .findFirst()
            .orElse(null);
        if (playlistToDelete != null) {
            playlistService.deleteList(playlistToDelete);
            userPlaylist = playlistService.getUserLists(loggedInUser);
            if (selectedPlaylist != null && selectedPlaylist.getListName().equals(playlistName)) {
                selectedPlaylist = null;
                selectedPlaylistMovies = null;
            }
        }
    }

    // Getters and Setters
    public List<UserPlaylist> getUserPlaylist() { return userPlaylist; }
    public void setUserPlaylist(List<UserPlaylist> userPlaylist) { this.userPlaylist = userPlaylist; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public UserMovieRating.Status getStatus() { return status; }
    public void setStatus(UserMovieRating.Status status) { this.status = status; }
    public String getListName() { return listName; }
    public void setListName(String listName) { this.listName = listName; }
    public UserPlaylist getSelectedPlaylist() { return selectedPlaylist; }
    public void setSelectedPlaylist(UserPlaylist selectedPlaylist) { this.selectedPlaylist = selectedPlaylist; }
    public List<UserMovieRatingDTO> getSelectedPlaylistMovies() { return selectedPlaylistMovies; }
    public void setSelectedPlaylistMovies(List<UserMovieRatingDTO> selectedPlaylistMovies) { this.selectedPlaylistMovies = selectedPlaylistMovies; }
    public int getRatingAsInt() { return (int) Math.round(rating); }
    public void setRatingAsInt(int ratingAsInt) { this.rating = ratingAsInt; }
    public int getMovieRatingAsInt(UserMovieRatingDTO movie) { return movie != null && movie.getRating() != null ? movie.getRatingAsInt() : 0; }
    public Movie getSelectedMovie() { return selectedMovie; }
    public void setSelectedMovie(Movie selectedMovie) { this.selectedMovie = selectedMovie; }
}