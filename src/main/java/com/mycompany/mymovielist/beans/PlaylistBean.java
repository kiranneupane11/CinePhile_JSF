/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mymovielist.beans;
import com.mycompany.mymovielist.model.*;
import com.mycompany.mymovielist.service.*;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.inject.Inject;
import javax.faces.context.FacesContext;
import javax.faces.application.FacesMessage;


/**
 *
 * @author kiran
 */
@Named
@ViewScoped
public class PlaylistBean implements Serializable{
   
    @Inject
    private PlaylistService playlistService;
    
    @Inject
    private AuthenticationBean authenticationBean;
    
    private User loggedInUser;
    private List<UserPlaylist> userPlaylist;
    private double rating;
    private UserMovieRating.Status status;
    private String listName;
    private UserMovieRatingDTO userMovieRatingDTO;
    private UserPlaylist selectedPlaylist;
    private List<UserMovieRatingDTO> selectedPlaylistMovies;
    
    @PostConstruct
    public void init() {
        this.loggedInUser = authenticationBean.getLoggedInUser();
    }
    
    public void addToList(Movie movie){
        userPlaylist = playlistService.getUserLists(loggedInUser);
        if (loggedInUser == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "You must be logged in to add movies to a list."));
            return;
        }
        UserMovieRating userMovieRating = new UserMovieRating(movie, rating, status, loggedInUser);
        if (playlistService.addMovieToList(loggedInUser, userMovieRating, listName)) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Movie added successfully!"));
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to add movie."));
        }
    }
    
   
    
    public List<UserPlaylist> getUserPlaylist() {
        return userPlaylist;
    }

    public void setUserPlaylist(List<UserPlaylist> userPlaylist) {
        this.userPlaylist = userPlaylist;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public UserMovieRating.Status getStatus() {
        return status;
    }

    public void setStatus(UserMovieRating.Status status) {
        this.status = status;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }
    
    public PlaylistService getPlaylistService() {
        return playlistService;
    }

    public void setPlaylistService(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }
    
     public UserMovieRatingDTO getUserMovieRatingDTO() {
        return userMovieRatingDTO;
    }

    public void setUserMovieRatingDTO(UserMovieRatingDTO userMovieRatingDTO) {
        this.userMovieRatingDTO = userMovieRatingDTO;
    }
    
    public UserPlaylist getSelectedPlaylist() {
        return selectedPlaylist;
    }

    public void setSelectedPlaylist(UserPlaylist selectedPlaylist) {
        this.selectedPlaylist = selectedPlaylist;
    }
    
    public void selectPlaylist(UserPlaylist playlist) {
        this.selectedPlaylist = playlist;
        this.selectedPlaylistMovies = playlistService.viewList(playlist, loggedInUser);
    }
    
    public void deleteList(UserPlaylist playlist) {
        playlistService.deleteList(playlist);
        userPlaylist = playlistService.getUserLists(loggedInUser);
        selectedPlaylist = null;
        selectedPlaylistMovies = null;
    }
    
    public List<UserMovieRatingDTO> getSelectedPlaylistMovies() {
        return selectedPlaylistMovies;
    }

    public void setSelectedPlaylistMovies(List<UserMovieRatingDTO> selectedPlaylistMovies) {
        this.selectedPlaylistMovies = selectedPlaylistMovies;
    }
    
    public int getRatingAsInt() {
        return (int) Math.round(rating);
    }

    public void setRatingAsInt(int ratingAsInt) {
        this.rating = ratingAsInt;
    }
    
    public int getMovieRatingAsInt(UserMovieRatingDTO movie) {
        return movie != null && movie.getRating() != null ? (int) Math.round(movie.getRating()) : 0;
}
    
}
