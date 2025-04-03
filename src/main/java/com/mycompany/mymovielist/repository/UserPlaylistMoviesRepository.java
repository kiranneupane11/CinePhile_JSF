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
import javax.persistence.EntityManager;


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
        EntityManager em = EMFProvider.getEntityManager();
        try {
            return em.createQuery(
                "SELECT new com.mycompany.mymovielist.model.UserMovieRatingDTO(" +
                    "upm.movie, " +
                    "(SELECT umr FROM UserMovieRating umr WHERE umr.movie = upm.movie AND umr.user = :userIdParam)" +
                ") " +
                "FROM UserPlaylistMovies upm " +
                "WHERE upm.userPlaylist.id = :playlistIdParam", UserMovieRatingDTO.class)
                .setParameter("playlistIdParam", playlist.getId())
                .setParameter("userIdParam", user)
                .getResultList();
        } finally {
            em.close();
        }
    }

    
    public void removeMovieFromPlaylist(UserPlaylist playlist, Movie movie) {
        // Ensure we're using managed entities
        UserPlaylist managedPlaylist = entityManager.merge(playlist);
        Movie managedMovie = entityManager.merge(movie);

        int result = entityManager.createQuery(
            "DELETE FROM UserPlaylistMovies upm " +
            "WHERE upm.userPlaylist = :playlist " +
            "AND upm.movie = :movie")
            .setParameter("playlist", managedPlaylist)
            .setParameter("movie", managedMovie)
            .executeUpdate();

        System.out.println("Rows removed: " + result);
    }
    
    public void removeByPlaylist(UserPlaylist playlist) {
        entityManager.createQuery("DELETE FROM UserPlaylistMovies upm WHERE upm.userPlaylist = :playlist")
            .setParameter("playlist", playlist)
            .executeUpdate();
    }

} 
