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

    @Inject
    public PlaylistService(UserPlaylistRepository userPlaylistRepository,
                           UserPlaylistMoviesRepository userPlayListMoviesRepository,
                           UserMovieRatingRepository userMovieRepository) {
        this.userPlaylistRepository = userPlaylistRepository;
        this.userPlayListMoviesRepository = userPlayListMoviesRepository;
        this.userMovieRepository = userMovieRepository;
    }
    
    public PlaylistService(){};

    public void createList(User user, String listName) {
        if (!listExists(user, listName)) {
            UserPlaylist playlist = new UserPlaylist(listName, user);
            userPlaylistRepository.add(playlist.getId(), playlist);
        }
    }

    public boolean addMovieToList(User user, UserMovieRating userMovieRating, String listName) {
        Optional<UserPlaylist> existingListOpt = userPlaylistRepository.findByUserIdAndListName(user, listName);
        UserPlaylist playlist = existingListOpt.orElseGet(() -> {
            UserPlaylist newPlaylist = new UserPlaylist(listName, user);
            userPlaylistRepository.add(newPlaylist.getId(), newPlaylist);
            return newPlaylist;
        });

        UserPlaylistMovies userPlaylistMovie = new UserPlaylistMovies(playlist, userMovieRating.getMovieID());
        userPlayListMoviesRepository.add(userPlaylistMovie.getId(), userPlaylistMovie);
        userMovieRepository.add(userMovieRating.getId(), userMovieRating);
        return true;
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

    public List<UserMovieRatingDTO> viewList(UserPlaylist playlist, User user) {
        return userPlayListMoviesRepository.getMoviesFromPlaylist(playlist, user);
    }

    public Optional<UserPlaylist> getUserPlaylistById(long playlistId, User user) {
        return userPlaylistRepository.get(playlistId).filter(p -> p.getUser().equals(user));
    }

    public void deleteList(UserPlaylist playlist) {
        userPlayListMoviesRepository.removeByPlaylist(playlist);
        userPlaylistRepository.remove(playlist.getId());
    }
}