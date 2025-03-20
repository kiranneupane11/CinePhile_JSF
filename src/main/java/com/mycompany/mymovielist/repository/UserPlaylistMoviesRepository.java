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
            "SELECT new com.mycompany.mymovielist.model.UserMovieRatingDTO(m, umr) " +
            "FROM UserPlaylistMovies upm " +
            "JOIN upm.userPlaylist pl " +
            "JOIN upm.movie m " +
            "LEFT JOIN UserMovieRating umr ON umr.movie = m AND umr.user = :userIdParam " +
            "WHERE pl.id = :playlistIdParam", UserMovieRatingDTO.class)
            .setParameter("playlistIdParam", playlist.getId())
            .setParameter("userIdParam", user)
            .getResultList();
    }
    
    public void removeByPlaylist(UserPlaylist playlist) {
        entityManager.createQuery("DELETE FROM UserPlaylistMovies upm WHERE upm.userPlaylist = :playlist")
            .setParameter("playlist", playlist)
            .executeUpdate();
    }

} 
