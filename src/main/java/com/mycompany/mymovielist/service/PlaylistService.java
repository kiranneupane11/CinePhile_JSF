package com.mycompany.mymovielist.service;

import com.mycompany.mymovielist.model.*;
import com.mycompany.mymovielist.repository.*;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Optional;
import com.mycompany.mymovielist.util.EMFProvider;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;


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
    
    private EntityManager getEntityManager() {
        return EMFProvider.getEntityManager();
    }

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
    
    public boolean removeUserPlaylistMovie(UserPlaylist playlist, Movie movie, User user) {
    EntityManager em = null;
    EntityTransaction tx = null;
    try {
        em = EMFProvider.getEntityManager();
        tx = em.getTransaction();
        tx.begin();

        // Ensure the playlist is owned by the user
        UserPlaylist managedPlaylist = em.find(UserPlaylist.class, playlist.getId());
        if (!managedPlaylist.getUser().getId().equals(user.getId())) {
            System.out.println("User does not own the playlist.");
            return false;
        }
        
        // Find the managed Movie entity
        Movie managedMovie = em.find(Movie.class, movie.getId());
        if (managedMovie == null) {
            System.out.println("Movie not found.");
            return false;
        }

        // Locate the UserPlaylistMovies entity that represents the relationship.
        TypedQuery<UserPlaylistMovies> query = em.createQuery(
            "SELECT upm FROM UserPlaylistMovies upm WHERE upm.userPlaylist = :playlist AND upm.movie = :movie", 
            UserPlaylistMovies.class);
        query.setParameter("playlist", managedPlaylist);
        query.setParameter("movie", managedMovie);
        UserPlaylistMovies upm = query.getSingleResult();
        
        if (upm != null) {
            em.remove(upm);
        } else {
            System.out.println("UserPlaylistMovies record not found.");
            return false;
        }
        
        tx.commit();
        return true;
    } catch (Exception e) {
        if (tx != null && tx.isActive()) {
            tx.rollback();
        }
        System.err.println("Remove failed: " + e.getMessage());
        return false;
    } finally {
        if (em != null) {
            em.close();
        }
    }
}


    public void deleteList(UserPlaylist playlist) {
        userPlayListMoviesRepository.removeByPlaylist(playlist);
        userPlaylistRepository.remove(playlist.getId());
    }
}