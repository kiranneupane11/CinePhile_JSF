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
import java.util.stream.Collectors;

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
    private List<PlaylistWithMovies> playlistsWithMovies;

    public static class PlaylistWithMovies implements Serializable {
        private UserPlaylist playlist;
        private List<UserMovieRatingDTO> movies;

        public PlaylistWithMovies(UserPlaylist playlist, List<UserMovieRatingDTO> movies) {
            this.playlist = playlist;
            this.movies = movies;
        }

        public UserPlaylist getPlaylist() {
            return playlist;
        }

        public List<UserMovieRatingDTO> getMovies() {
            return movies;
        }
    }

    @PostConstruct
    public void init() {
        this.loggedInUser = authenticationBean.getLoggedInUser();
        if (loggedInUser == null) {
            System.out.println("Logged-in user is null");
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning", "No user logged in."));
            return;
        }
        System.out.println("Logged-in user: " + loggedInUser.getId());
        userPlaylist = playlistService.getUserLists(loggedInUser);
        if (userPlaylist == null) {
            System.out.println("User playlist is null");
            return;
        }
        System.out.println("User playlists size: " + userPlaylist.size());
        String[] defaultLists = {"Watching", "Watched", "Plan to Watch", "Dropped"};
        for (String list : defaultLists) {
            if (!playlistService.listExists(loggedInUser, list)) {
                playlistService.createList(loggedInUser, list);
                System.out.println("Created list: " + list);
            }
        }
        loadPlaylistsWithMovies();
    }

    private void loadPlaylistsWithMovies() {
        if (loggedInUser == null) {
            System.out.println("Cannot load playlists: loggedInUser is null");
            return;
        }
        userPlaylist = playlistService.getUserLists(loggedInUser);
        if (userPlaylist == null) {
            System.out.println("User playlist is null in loadPlaylistsWithMovies");
            return;
        }
        try {
            playlistsWithMovies = userPlaylist.stream()
                .peek(p -> System.out.println("Processing playlist: " + p.getListName() + " (ID: " + p.getId() + ")"))
                .map(p -> {
                    List<UserMovieRatingDTO> movies = playlistService.viewMoviesFromPlaylist(p, loggedInUser);
                    System.out.println("Movies for " + p.getListName() + ": " + (movies != null ? movies.size() : "null"));
                    return new PlaylistWithMovies(p, movies);
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error loading playlists with movies: " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to load playlists: " + e.getMessage()));
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
        if (status == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Please select a status."));
            return;
        }
        try {
            // Use the new addOrUpdateMovieRating method
            playlistService.addOrUpdateMovieRating(loggedInUser, selectedMovie, rating, status);
            listName = status.toString().replace("_", " ");
            if (playlistService.addMovieToList(loggedInUser, new UserMovieRating(selectedMovie, rating, status, loggedInUser), listName)) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Movie added successfully!"));
                loadPlaylistsWithMovies();
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to add movie to playlist."));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to add movie: " + e.getMessage()));
        }
    }

    public void selectPlaylist(String playlistName) {
        if (loggedInUser == null) {
            return;
        }
        userPlaylist = playlistService.getUserLists(loggedInUser);
        selectedPlaylist = userPlaylist.stream()
            .filter(p -> p.getListName().equals(playlistName))
            .findFirst()
            .orElse(null);
        if (selectedPlaylist != null) {
            selectedPlaylistMovies = playlistService.viewMoviesFromPlaylist(selectedPlaylist, loggedInUser);
        } else {
            selectedPlaylistMovies = null;
        }
    }

    public void deleteList(String playlistName) {
        if (loggedInUser == null) {
            return;
        }
        userPlaylist = playlistService.getUserLists(loggedInUser);
        UserPlaylist playlistToDelete = userPlaylist.stream()
            .filter(p -> p.getListName().equals(playlistName))
            .findFirst()
            .orElse(null);
        if (playlistToDelete != null) {
            try {
                playlistService.deleteList(playlistToDelete);
                loadPlaylistsWithMovies();
                if (selectedPlaylist != null && selectedPlaylist.getListName().equals(playlistName)) {
                    selectedPlaylist = null;
                    selectedPlaylistMovies = null;
                }
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Playlist deleted successfully!"));
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to delete playlist: " + e.getMessage()));
            }
        }
    }

    public void removeMovieFromPlaylist(PlaylistWithMovies playlistWrapper, UserMovieRatingDTO movieRating) {
        if (loggedInUser == null || playlistWrapper == null || movieRating == null) {
            return;
        }
        UserPlaylist playlist = playlistWrapper.getPlaylist();
        try {
            playlistService.removeMovieFromList(playlist, movieRating, loggedInUser);
            loadPlaylistsWithMovies();
            if (selectedPlaylist != null && selectedPlaylist.equals(playlist)) {
                selectedPlaylistMovies = playlistService.viewMoviesFromPlaylist(selectedPlaylist, loggedInUser);
            }
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Movie removed successfully!"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to remove movie: " + e.getMessage()));
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
    public List<PlaylistWithMovies> getPlaylistsWithMovies() { return playlistsWithMovies; }
}