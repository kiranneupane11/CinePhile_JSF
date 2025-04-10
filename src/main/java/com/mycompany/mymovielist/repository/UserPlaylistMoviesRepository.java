/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mymovielist.repository;

import com.mycompany.mymovielist.model.*;
import com.mycompany.mymovielist.util.EMFProvider;
import java.util.*;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.TypedQuery;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.criteria.*;
/**
 *
 * @author kiran
 */
@Named
@ApplicationScoped
public class UserPlaylistMoviesRepository extends DatabaseRepository<UserPlaylistMovies, Long> {
    @Inject
    public UserPlaylistMoviesRepository() {
        super(UserPlaylistMovies.class, EMFProvider.getEntityManager());
    }
    
    public List<UserMovieRatingDTO> getMoviesFromPlaylist(UserPlaylist playlist, User user) {
            return entityManager.createQuery(
                "SELECT new com.mycompany.mymovielist.model.UserMovieRatingDTO(" +
                    "upm.movie, " +
                    "(SELECT umr FROM UserMovieRating umr WHERE umr.movie = upm.movie AND umr.user = :userIdParam)" +
                ") " +
                "FROM UserPlaylistMovies upm " +
                "WHERE upm.userPlaylist.id = :playlistIdParam", UserMovieRatingDTO.class)
                .setParameter("playlistIdParam", playlist.getId())
                .setParameter("userIdParam", user)
                .setHint("javax.persistence.cache.storeMode", "REFRESH")
                .getResultList();
    }
    
    public void removeByPlaylist(UserPlaylist playlist) {
        entityManager.createQuery("DELETE FROM UserPlaylistMovies upm WHERE upm.userPlaylist = :playlist")
            .setParameter("playlist", playlist)
            .executeUpdate();
    }
    
    public void removeAssociation(UserPlaylist playlist, Movie movie) {
    EntityManager em = EMFProvider.getEntityManager();
    EntityTransaction tx = em.getTransaction();
    try {
        tx.begin();
        int count = em.createQuery(
            "DELETE FROM UserPlaylistMovies upm WHERE upm.userPlaylist.id = :playlistId AND upm.movie.id = :movieId")
            .setParameter("playlistId", playlist.getId())
            .setParameter("movieId", movie.getId())
            .executeUpdate();
        tx.commit();
        em.clear();
        System.out.println("Deleted " + count + " association(s) for movie: " + movie.getTitle());
    } catch (Exception e) {
        if (tx.isActive()) {
            tx.rollback();
        }
        throw e;
    } finally {
        em.close();
    }
}
    
    public Optional<UserPlaylistMovies> findByPlaylistAndMovie(UserPlaylist playlist, Movie movie) {
        TypedQuery<UserPlaylistMovies> query = entityManager.createQuery(
            "SELECT upm FROM UserPlaylistMovies upm WHERE upm.userPlaylist = :playlist AND upm.movie = :movie",
            UserPlaylistMovies.class);
        query.setParameter("playlist", playlist);
        query.setParameter("movie", movie);
        List<UserPlaylistMovies> result = query.getResultList();
        return result.stream().findFirst();
    }

    
   public void removeAssociationsExcept(User user, Movie movie, UserPlaylist targetPlaylist) {
        EntityManager em = EMFProvider.getEntityManager();
        try {
            EntityTransaction tx = em.getTransaction();
            tx.begin();
            int count = em.createQuery(
                "DELETE FROM UserPlaylistMovies upm " +
                "WHERE upm.movie = :movie " +
                "AND upm.userPlaylist.id IN (" +
                "    SELECT up.id FROM UserPlaylist up " +
                "    WHERE up.user = :user AND up <> :targetPlaylist" +
                ")")
                .setParameter("movie", movie)
                .setParameter("user", user)
                .setParameter("targetPlaylist", targetPlaylist)
                .executeUpdate();
            tx.commit();
            System.out.println("Deleted " + count + " associations");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
   
    public List<UserMovieRatingDTO> getMoviesFromPlaylistLazy(UserPlaylist playlist, User user, int pageNumber, int pageSize) {
    // First, retrieve movies from the playlist.
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Movie> movieCq = cb.createQuery(Movie.class);
    Root<UserPlaylistMovies> upm = movieCq.from(UserPlaylistMovies.class);
    Join<UserPlaylistMovies, Movie> movieJoin = upm.join("movie", JoinType.INNER);
    movieCq.select(movieJoin)
           .where(cb.equal(upm.get("userPlaylist"), playlist));
    
    List<Movie> movies = entityManager.createQuery(movieCq)
                               .setHint("javax.persistence.cache.storeMode", "REFRESH")
                               .setFirstResult((pageNumber - 1) * pageSize)
                                .setMaxResults(pageSize)
                               .getResultList();
    
    // Now, for each movie, fetch its corresponding UserMovieRating
    List<UserMovieRatingDTO> result = new ArrayList<>();
    for (Movie movie : movies) {
        CriteriaQuery<UserMovieRating> ratingCq = cb.createQuery(UserMovieRating.class);
        Root<UserMovieRating> umr = ratingCq.from(UserMovieRating.class);
        ratingCq.select(umr)
                .where(
                    cb.equal(umr.get("movie"), movie),
                    cb.equal(umr.get("user"), user)
                );
        
        UserMovieRating rating = entityManager.createQuery(ratingCq)
                                      .setHint("javax.persistence.cache.storeMode", "REFRESH")
                                      .getSingleResult();
                                      
        // Construct DTO (assuming the DTO has a constructor accepting a Movie and a UserMovieRating)
        result.add(new UserMovieRatingDTO(movie, rating));
    }
    
    return result;
}


} 
