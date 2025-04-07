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
import javax.transaction.Transactional;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
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


} 
