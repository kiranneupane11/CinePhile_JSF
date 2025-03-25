package com.mycompany.mymovielist.service;

import com.mycompany.mymovielist.model.*;
import com.mycompany.mymovielist.repository.*;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Optional;

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
            // Check if there is an existing rating for this movie by the user
            UserMovieRating movieRating = userMovieRepository.findByMovieAndUser(movie, user);

            if (movieRating == null) {
                // Create a new rating instance if not found
                movieRating = new UserMovieRating(movie, rating, status, user);
                userMovieRepository.add(movieRating);
            } else {
                // Update the existing rating
                movieRating.setRating(rating);
                movieRating.setStatus(status);
                userMovieRepository.update(movieRating);
            }

            // Now add the movie to the corresponding playlist
            Optional<UserPlaylist> existingListOpt = userPlaylistRepository.findByUserIdAndListName(user, listName);
            UserPlaylist playlist = existingListOpt.orElseGet(() -> {
                UserPlaylist newPlaylist = new UserPlaylist(listName, user);
                userPlaylistRepository.add(newPlaylist);
                return newPlaylist;
            });

            // Create the association between the playlist and the movie rating
            UserPlaylistMovies userPlaylistMovie = new UserPlaylistMovies(playlist, movieRating.getMovieID());
            userPlayListMoviesRepository.add(userPlaylistMovie);
            return true;
        } catch (Exception e) {
            // Log the exception and maybe rethrow it or return false
            e.printStackTrace();
            return false;
        }
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

    public Optional<UserPlaylist> getUserPlaylistById(long playlistId, User user) {
        return userPlaylistRepository.get(playlistId).filter(p -> p.getUser().equals(user));
    }
    
    public boolean removeMovieFromList(UserPlaylist playlist, UserMovieRatingDTO userMovie, User user) {
        if (!playlist.getUser().equals(user)) {
            return false;
        }
        Long movieId = userMovie.getMovieID();
        if (movieId == null) {
            return false;
        }
        Optional<Movie> movie = movieRepository.get(movieId);
        if (!movie.isPresent()) {
            return false;
        }
        userPlayListMoviesRepository.removeMovieFromPlaylist(playlist, movie.get());
    return true;
}

    public void deleteList(UserPlaylist playlist) {
        userPlayListMoviesRepository.removeByPlaylist(playlist);
        userPlaylistRepository.remove(playlist.getId());
    }
}