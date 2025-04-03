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
import java.util.Optional;
import org.primefaces.event.TabChangeEvent;



@Named
@ViewScoped
public class PlaylistBean implements Serializable {
    @Inject
    private PlaylistService playlistService;
    
    @Inject
    private MovieService movieService;

    @Inject
    private AuthenticationBean authenticationBean;

    private User loggedInUser;
    private List<UserPlaylist> userPlaylist;
    private int rating; 
    private String status; 
    private String listName;
    private UserPlaylist selectedPlaylist;
    private List<UserMovieRatingDTO> selectedPlaylistMovies;
    private Movie selectedMovie;
    private List<PlaylistDTO> playlistsWithMovies;
    private Long movieId;
    private boolean isEditing = false;

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
        
        if (status == null) {
        status = "Plan_To_Watch"; 
        }
        rating = 0;
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
        try {
            playlistsWithMovies = playlistService.loadPlaylistsWithMovies(loggedInUser);
        } catch (Exception e) {
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
        if (status == null || status.trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Please select a status."));
            return;
        }
        try {
            // Convert String status to enum (adjust the conversion as needed)
            UserMovieRating.Status enumStatus = UserMovieRating.Status.valueOf(status.replace(" ", "_"));

            double ratingToUse = rating;
            
            // Map status to listName for display purposes (remove underscores)
            listName = status.replace("_", " ");

            // Use the combined method
            boolean success = playlistService.addOrUpdateMovieInList(loggedInUser, selectedMovie, ratingToUse, enumStatus, listName);
            if (success) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Movie added successfully!"));
                loadPlaylistsWithMovies();
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to add movie to playlist."));
            }
        } catch (IllegalArgumentException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Invalid status value: " + status));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to add movie: " + e.getMessage()));
        }
    }
    
    public void updateMovieInList() {
        try {
            UserMovieRating.Status enumStatus = UserMovieRating.Status.valueOf(status);
            double ratingToUse = rating;
            listName = status.replace("_", " ");
            boolean success = playlistService.addOrUpdateMovieInList(loggedInUser, selectedMovie, ratingToUse, enumStatus, listName);
            this.isEditing = false;
            if (success) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Movie updated successfully!"));
                loadPlaylistsWithMovies();
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to update movie in playlist."));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to update movie: " + e.getMessage()));
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

    public void removeMovieFromPlaylist(PlaylistDTO playlistWrapper, UserMovieRatingDTO usermovieRatingDTO) {
        // First, fetch the full playlist entity using its ID from the DTO.
        Optional<UserPlaylist> fetchedPlaylist = playlistService.getPlaylist(playlistWrapper.getPlaylistId(), loggedInUser);
        if (!fetchedPlaylist.isPresent()) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to fetch playlist."));
            return;
        }

        // Next, fetch the full movie entity using the movie ID from the UserMovieRatingDTO.
        Optional<Movie> optionalMovie = movieService.getMovieById(usermovieRatingDTO.getMovieID());
        if (!optionalMovie.isPresent()) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Movie not found."));
            return;
        }

        // Call the service method to remove the movie from the playlist.
        boolean removed = playlistService.removeMovieInPlaylist(fetchedPlaylist.get(), optionalMovie.get(), loggedInUser);
        if (removed) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Movie removed successfully!"));
            loadPlaylistsWithMovies();
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to remove movie from playlist."));
        }
    }


    public void editMovieFromPlaylist(PlaylistDTO playlistWrapper, UserMovieRatingDTO movieRating) {
        this.isEditing = true;
        // Fetch and set the correct playlist for this edit action
        Optional<UserPlaylist> fetchedPlaylist = playlistService.getPlaylist(playlistWrapper.getPlaylistId(), loggedInUser);
        if (fetchedPlaylist.isPresent()) {
             this.selectedPlaylist = fetchedPlaylist.get();
        } else {
             FacesContext.getCurrentInstance().addMessage(null,
                 new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to fetch playlist."));
             return;
        }
        // Set the movie id and load the movie entity for the selected rating
        this.movieId = movieRating.getMovieID();
        Optional<Movie> freshMovie = movieService.getMovieById(movieId);
        if (freshMovie.isPresent()) {
             this.selectedMovie = freshMovie.get();
             System.out.println("Selected movie for editing: " + selectedMovie.getTitle());
        } else {
             FacesContext.getCurrentInstance().addMessage(null,
                 new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to fetch movie."));
        }
        // Update status and rating from the DTO
        this.status = movieRating.getStatus() != null ? movieRating.getStatus().toString() : "Plan_To_Watch";
        this.rating = movieRating.getRating() != null ? movieRating.getRatingAsInt() : 0;
    }


    
    public void loadSelectedMovie() {
        if (!isEditing) {
            if (movieId != null) {
                Optional<Movie> retrievedMovie = movieService.getMovieById(movieId);
                if (retrievedMovie.isPresent()) {
                    selectedMovie = retrievedMovie.get();
                } else {
                    FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Movie not found."));
                }
            }
        }
    }
    
     public void onTabChange(TabChangeEvent event) {
        loadPlaylistsWithMovies();
    }


    // Getters and Setters
    public List<UserPlaylist> getUserPlaylist() { return userPlaylist; }
    public void setUserPlaylist(List<UserPlaylist> userPlaylist) { this.userPlaylist = userPlaylist; }
    public double getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getStatus() { return status; } 
    public void setStatus(String status) { System.out.println("Setting status: " + status);this.status = status; } 
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
    public List<PlaylistDTO> getPlaylistsWithMovies() { return playlistsWithMovies; }
    
    public Long getMovieId() {
        return movieId;
    }
    public void setMovieId(Long movieId) {
        System.out.println("setMovieId called with: " + movieId);
        this.movieId = movieId;
    }

    public boolean isEditing() {
        return isEditing;
    }

    public void setEditing(boolean isEditing) {
            this.isEditing = isEditing;
        }
    }