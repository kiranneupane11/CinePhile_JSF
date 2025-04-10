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
import java.util.*;
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
    private List<PlaylistDTO> playlists;
    private Long movieId;
    private boolean isEditing = false;
    private Long editPlaylistId;
    private Long editMovieId;
    private Movie editMovie;
    private String editStatus;
    private int editRating;
    private PlaylistDTO removePlaylistWrapper;
    private UserMovieRatingDTO removeMovieRating;
    private Map<Long, Integer> playlistPageMap = new HashMap<>();
    private final int pageSize = 3;

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
        
        // Load playlists lazily (only metadata initially)
        playlists = playlistService.loadPlaylistsMetadata(loggedInUser);
        
        String mId = FacesContext.getCurrentInstance().getExternalContext()
                            .getRequestParameterMap().get("movieId");
            if (mId != null && !mId.trim().isEmpty()) {
                try {
                    Long initialMovieId = Long.valueOf(mId);
                    // Load the movie once if needed, then immediately clear the property.
                    Optional<Movie> m = movieService.getMovieById(initialMovieId);
                    if (m.isPresent()) {
                        selectedMovie = m.get();
                        System.out.println("Initial movie loaded: " + selectedMovie.getTitle());
                    }
                } catch(NumberFormatException e) {
                    System.out.println("Invalid movieId parameter: " + mId);
                }
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
            // Convert String status to enum 
            UserMovieRating.Status enumStatus = UserMovieRating.Status.valueOf(status.replace(" ", "_"));

            double ratingToUse = rating;
            
            listName = status.replace("_", " ");

            boolean success = playlistService.addOrUpdateMovieInList(loggedInUser, selectedMovie, ratingToUse, enumStatus, listName);
            if (success) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Movie added successfully!"));
                playlists = playlistService.loadPlaylistsMetadata(loggedInUser);
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
                playlists = playlistService.loadPlaylistsMetadata(loggedInUser);
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
                playlists = playlistService.loadPlaylistsMetadata(loggedInUser);
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

    public void prepareRemoveDialog(PlaylistDTO playlistWrapper, UserMovieRatingDTO movieRating) {
        this.removePlaylistWrapper = playlistWrapper;
        this.removeMovieRating = movieRating;
        // log details
        System.out.println("Preparing to remove movie: " + movieRating.getTitle() +
                           " from playlist: " + playlistWrapper.getListName());
    }
    
    public void confirmRemove() {
    try {
        boolean success = playlistService.removeMovieFromPlaylist(loggedInUser, removePlaylistWrapper, removeMovieRating);
        if (success) {
            playlists = playlistService.loadPlaylistsMetadata(loggedInUser);
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Movie removed from playlist successfully!"));
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to remove movie from playlist."));
        }
    } catch (Exception e) {
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "An error occurred: " + e.getMessage()));
        e.printStackTrace();
    }
}




   public void prepareEditMovie(PlaylistDTO playlist, UserMovieRatingDTO movieRating) {
    try {
        this.editPlaylistId = playlist.getPlaylistId();
        this.editMovieId = movieRating.getMovieId();
        this.editRating = movieRating.getRating() != null ? movieRating.getRatingAsInt() : 0;
        this.editStatus = movieRating.getStatus() != null ? movieRating.getStatus().toString() : "Plan_To_Watch";
        
        System.out.println("Playlist Id: " + editPlaylistId + " / Username: " + loggedInUser.getUsername());
        
        Optional<UserPlaylist> fetchedPlaylist = playlistService.getPlaylist(editPlaylistId, loggedInUser);
        if (!fetchedPlaylist.isPresent()) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to fetch playlist."));
            return;
        }
        Optional<Movie> freshMovie = movieService.getMovieById(editMovieId);
        if (!freshMovie.isPresent()) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to fetch movie."));
            return;
        }
        this.editMovie = freshMovie.get();
        System.out.println("Preparing edit for movie: " + editMovie.getTitle() +
                           " from playlist id: " + editPlaylistId +
                           ", status: " + editStatus +
                           ", rating: " + editRating);
        FacesContext.getCurrentInstance().getViewRoot().clearInitialState();
    } catch (Exception e) {
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "An error occurred: " + e.getMessage()));
    }
}

   
    public void updateEditMovieInList() {
        try {
            UserMovieRating.Status enumStatus = UserMovieRating.Status.valueOf(editStatus);
            double ratingToUse = editRating;
            String listName = editStatus.replace("_", " ");
            boolean success = playlistService.addOrUpdateMovieInList(loggedInUser, editMovie, ratingToUse, enumStatus, listName);
            this.isEditing = false;
            if (success) {
                playlists = playlistService.loadPlaylistsMetadata(loggedInUser);
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Movie updated successfully!"));
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to update movie in playlist."));
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to update movie: " + e.getMessage()));
        }
    }   
    
    public void loadSelectedMovie() {
        if (!isEditing && selectedMovie == null && movieId != null) {
            Optional<Movie> retrievedMovie = movieService.getMovieById(movieId);
            if (retrievedMovie.isPresent()) {
                selectedMovie = retrievedMovie.get();
                System.out.println("Movie loaded from URL: " + selectedMovie.getTitle());
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Movie not found."));
            }
        }
    }
    
    public void onTabChange(TabChangeEvent event) {
        Object attr = event.getTab().getAttributes().get("playlistId");
        if (attr != null) {
            final Long playlistId;
            try {
                playlistId = Long.valueOf(attr.toString());
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return;
            }
            // Find the corresponding PlaylistDTO in our lazy-loaded list
            Optional<PlaylistDTO> dtoOpt = playlists.stream()
                    .filter(dto -> dto.getPlaylistId().equals(playlistId))
                    .findFirst();
            if (dtoOpt.isPresent()) {
                PlaylistDTO dto = dtoOpt.get();
                // Only load movies if not already loaded
                if (dto.getMovies() == null || dto.getMovies().isEmpty()) {
                    Optional<UserPlaylist> plOpt = playlistService.getPlaylist(playlistId, loggedInUser);
                    if (plOpt.isPresent()) {
                        List<UserMovieRatingDTO> firstPage = playlistService.viewMoviesFromPlaylistLazy(plOpt.get(), loggedInUser, 1, pageSize);
                    dto.setMovies(new ArrayList<>(firstPage));
                    playlistPageMap.put(playlistId, 2);
                    }
                }
            }
        }
    }
    
    public void loadNextPage(Long playlistId) {
    int currentPage = playlistPageMap.getOrDefault(playlistId, 1);
    Optional<PlaylistDTO> dtoOpt = playlists.stream()
            .filter(dto -> dto.getPlaylistId().equals(playlistId))
            .findFirst();
    if (dtoOpt.isPresent()) {
        PlaylistDTO dto = dtoOpt.get();
        Optional<UserPlaylist> plOpt = playlistService.getPlaylist(playlistId, loggedInUser);
        if (plOpt.isPresent()) {
            List<UserMovieRatingDTO> nextPage = playlistService.viewMoviesFromPlaylistLazy(plOpt.get(), loggedInUser, currentPage, pageSize);
            if (!nextPage.isEmpty()) {
                dto.getMovies().addAll(nextPage);
                playlistPageMap.put(playlistId, currentPage + 1);
            }
        }
    }
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
    public List<PlaylistDTO> getPlaylists() {return playlists;}
    
    public Long getMovieId() {
        return movieId;
    }
    
    public void setMovieId(Long movieId) {
    if (!FacesContext.getCurrentInstance().isPostback()) {
        System.out.println("setMovieId called with: " + movieId);
        this.movieId = movieId;
    }
}

    public boolean isEditing() {
        return isEditing;
    }

    public void setEditing(boolean isEditing) {
            this.isEditing = isEditing;
        }
    
    public Long getEditPlaylistId() {
        return editPlaylistId;
    }
    public void setEditPlaylistId(Long editPlaylistId) {
        this.editPlaylistId = editPlaylistId;
    }

    public Long getEditMovieId() {
        return editMovieId;
    }
    public void setEditMovieId(Long editMovieId) {
        this.editMovieId = editMovieId;
    }

    public Movie getEditMovie() {
        return editMovie;
    }
    public void setEditMovie(Movie editMovie) {
        this.editMovie = editMovie;
    }

    public String getEditStatus() {
        return editStatus;
    }
    public void setEditStatus(String editStatus) {
        this.editStatus = editStatus;
    }

    public int getEditRating() {
        return editRating;
    }
    public void setEditRating(int editRating) {
        this.editRating = editRating;
    }
    
    public PlaylistDTO getRemovePlaylistWrapper() {
        return removePlaylistWrapper;
    }
    public void setRemovePlaylistWrapper(PlaylistDTO removePlaylistWrapper) {
        this.removePlaylistWrapper = removePlaylistWrapper;
    }

    public UserMovieRatingDTO getRemoveMovieRating() {
        return removeMovieRating;
    }
    public void setRemoveMovieRating(UserMovieRatingDTO removeMovieRating) {
        this.removeMovieRating = removeMovieRating;
    }

}
