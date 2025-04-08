package com.mycompany.mymovielist.service;

import com.mycompany.mymovielist.model.*;
import com.mycompany.mymovielist.repository.*;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;
import com.mycompany.mymovielist.util.EMFProvider;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;


@Named
@ApplicationScoped
public class PlaylistService {
    private UserPlaylistRepository userPlaylistRepository;
    private UserPlaylistMoviesRepository userPlayListMoviesRepository;
    private UserMovieRatingRepository userMovieRepository;
    private MovieRepository movieRepository;
    @Inject
    public PlaylistService(UserPlaylistRepository userPlaylistRepository,
                           UserPlaylistMoviesRepository userPlayListMoviesRepository,
                           UserMovieRatingRepository userMovieRepository,
                           MovieRepository movieRepository) {
        this.userPlaylistRepository = userPlaylistRepository;
        this.userPlayListMoviesRepository = userPlayListMoviesRepository;
        this.userMovieRepository = userMovieRepository;
        this.movieRepository = movieRepository;
    }
    
    public PlaylistService(){};

    public void createList(User user, String listName) {
        if (!listExists(user, listName)) {
            UserPlaylist playlist = new UserPlaylist(listName, user);
            userPlaylistRepository.add(playlist);
        }
    }
    
    public boolean addOrUpdateMovieInList(User user, Movie movie, Double rating, UserMovieRating.Status status, String listName) {
        try {
            // Update or create the rating
            Optional<UserMovieRating> fetchedMovieRating = userMovieRepository.findByUserAndMovie(user, movie);
            UserMovieRating movieRating;
            if (!fetchedMovieRating.isPresent()) {
                // Create new rating if not found
                movieRating = new UserMovieRating(movie, rating, status, user);
                userMovieRepository.add(movieRating);
            } else {
                // Update existing rating
                movieRating = fetchedMovieRating.get();
                movieRating.setRating(rating);
                movieRating.setStatus(status);
                userMovieRepository.update(movieRating);
            }

            // Find (or create) the target playlist for the given listName
            Optional<UserPlaylist> existingListOpt = userPlaylistRepository.findByUserIdAndListName(user, listName);
            UserPlaylist targetPlaylist = existingListOpt.orElseGet(() -> {
                UserPlaylist newPlaylist = new UserPlaylist(listName, user);
                userPlaylistRepository.add(newPlaylist);
                return newPlaylist;
            });

            // Remove any associations for this movie for this user that do not match the target playlist.
            userPlayListMoviesRepository.removeAssociationsExcept(user, movie, targetPlaylist);

            // Check if an association for the target playlist already exists; if not, create one.
            Optional<UserPlaylistMovies> existingAssociation = userPlayListMoviesRepository
                    .findByPlaylistAndMovie(targetPlaylist, movie);
            if (!existingAssociation.isPresent()) {
                UserPlaylistMovies association = new UserPlaylistMovies(targetPlaylist, movie);
                userPlayListMoviesRepository.add(association);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    
    public List<PlaylistDTO> loadPlaylistsWithMovies(User user) {
        List<UserPlaylist> playlists = getUserLists(user);
        List<PlaylistDTO> result = new ArrayList<>();

        for (UserPlaylist playlist : playlists) {
            List<UserMovieRatingDTO> movies = userPlayListMoviesRepository.getMoviesFromPlaylist(playlist, user);
            PlaylistDTO dto = new PlaylistDTO(playlist.getId(), playlist.getListName(), movies);
            result.add(dto);
        }

        return result;
    }


    public boolean listExists(User user, String listName) {
        return userPlaylistRepository.findByUserIdAndListName(user, listName).isPresent();
    }

    public List<UserPlaylist> getUserLists(User user) {
        return userPlaylistRepository.getListsByUserId(user);
    }

    public List<UserPlaylist> browseLists(String username) {
        return userPlaylistRepository.getListsByUserName(username);
    }

    public List<UserMovieRatingDTO> viewMoviesFromPlaylist(UserPlaylist playlist, User user) {
        return userPlayListMoviesRepository.getMoviesFromPlaylist(playlist, user);
    }

    public Optional<UserPlaylist> getPlaylist(Long listId, User user) {
        return userPlaylistRepository.getListById(listId, user);
    }
    
    public boolean removeMovieFromPlaylist(User user, PlaylistDTO playlistWrapper, UserMovieRatingDTO movieRating) {
    try {
        Optional<UserPlaylist> optionalPlaylist = userPlaylistRepository.getListById(playlistWrapper.getPlaylistId(), user);
        if (!optionalPlaylist.isPresent()) {
            throw new Exception("Playlist not found.");
        }
        UserPlaylist playlist = optionalPlaylist.get();

        Optional<Movie> optionalMovie = movieRepository.get(movieRating.getMovieId());
        if (!optionalMovie.isPresent()) {
            throw new Exception("Movie not found.");
        }
        Movie movie = optionalMovie.get();

        // Remove association based on IDs rather than object references.
        userPlayListMoviesRepository.removeAssociation(playlist, movie);

        // Option 1: Re-fetch the playlist to ensure fresh data.
        // This forces a new query, bypassing any cached state.
        Optional<UserPlaylist> refreshedPlaylist = userPlaylistRepository.getListById(playlistWrapper.getPlaylistId(), user);
        if (!refreshedPlaylist.isPresent()) {
            throw new Exception("Playlist not found during refresh.");
        }
        System.out.println("Refreshed playlist id after removal: " + refreshedPlaylist.get().getId());

        return true;
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}



    public void deleteList(UserPlaylist playlist) {
        userPlayListMoviesRepository.removeByPlaylist(playlist);
        userPlaylistRepository.remove(playlist);
    }
}