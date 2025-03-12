/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mymovielist.service;

import com.mycompany.mymovielist.model.*;
import com.mycompany.mymovielist.repository.*;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author kiran
 */
public class PlaylistService {
    private final UserPlaylistRepository userPlaylistRepository;
    private final UserPlaylistMoviesRepository userPlayListMoviesRepository;
    private final UserMovieRatingRepository userMovieRepository;
    private final MovieRepository movieRepository;

    public PlaylistService(UserPlaylistRepository userPlaylistRepository, 
                           UserPlaylistMoviesRepository userPlayListMoviesRepository,
                           UserMovieRatingRepository userMovieRepository,
                           MovieRepository movieRepository) {
        this.userPlaylistRepository = userPlaylistRepository;
        this.userPlayListMoviesRepository = userPlayListMoviesRepository;
        this.userMovieRepository = userMovieRepository;
        this.movieRepository = movieRepository;
    }

    public boolean addMovieToList(User user, UserMovieRating userMovieRating, UserPlaylist userPlaylist) {
        Optional<Movie> movieOpt = movieRepository.get(userMovieRating.getMovieID().getId());
        if (!movieOpt.isPresent()) return false;

        Optional<UserPlaylist> existingListOpt = userPlaylistRepository.findByUserIdAndListName(userPlaylist);
        if (existingListOpt.isPresent()) {
            userPlaylist = existingListOpt.get();
        } else {
            userPlaylist = new UserPlaylist(userPlaylist.getListName(), user);
            userPlaylistRepository.add(userPlaylist.getId(), userPlaylist);
        }

        UserPlaylistMovies userPlaylistMovie = new UserPlaylistMovies(userPlaylist, userMovieRating.getMovieID());
        userPlayListMoviesRepository.add(userPlaylistMovie.getId(), userPlaylistMovie);
        userMovieRepository.add(userMovieRating.getId(), userMovieRating);

        return true;
    }

    public List<UserPlaylist> getUserLists(User user) {
        return userPlaylistRepository.getListsByUserId(user);
    }
    
    public List<UserPlaylist> browseLists(String username) {
        return userPlaylistRepository.getListsByUserName(username);
    }

    public List<UserMovieRatingDTO> getMoviesInList(UserPlaylist playlist, User user) {
        return userPlayListMoviesRepository.getMoviesFromPlaylist(playlist, user);
    }

    public Optional<UserPlaylist> getUserPlaylistById(long playlistId, User user) {
        return userPlaylistRepository.get(playlistId)
                .filter(playlist -> playlist.getUser().equals(user));
    }
}
