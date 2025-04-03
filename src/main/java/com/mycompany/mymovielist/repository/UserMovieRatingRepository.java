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
import javax.persistence.NoResultException;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

/**
 *
 * @author kiran
 */
@Named
@ApplicationScoped
public class UserMovieRatingRepository extends DatabaseRepository<UserMovieRating, Long> {
    @Inject
    public UserMovieRatingRepository() {
        super(UserMovieRating.class, EMFProvider.getEntityManager());
    }
    
    public List<Object[]> getUserRatedMovies(UserMovieRating usermovie){
    return entityManager.createQuery(
        "SELECT um,m FROM UserMovieRating um JOIN Movie m ON um.movie = m.id WHERE um.user = :userId", 
        Object[].class)
        .setParameter("userId", usermovie.getUserID())
        .getResultList();
    }
    
    public Optional<UserMovieRating> findByUserAndMovie(User user, Movie movie) {
        EntityManager em = EMFProvider.getEntityManager();
        try {
            TypedQuery<UserMovieRating> query = em.createQuery(
                "SELECT umr FROM UserMovieRating umr WHERE umr.user = :user AND umr.movie = :movie",
                UserMovieRating.class);
            query.setParameter("user", user);
            query.setParameter("movie", movie);
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }
}
