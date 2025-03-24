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
    
    public UserMovieRating findByMovieAndUser(Movie movie, User user) {
        try {
            return entityManager.createQuery(
                "SELECT umr FROM UserMovieRating umr " +
                "WHERE umr.movie = :movie AND umr.user = :user", UserMovieRating.class)
                .setParameter("movie", movie)
                .setParameter("user", user)
                .getSingleResult();
        } catch (NoResultException e) {
            return null; 
        }
    }

    public void persist(UserMovieRating userMovieRating) {
        entityManager.getTransaction().begin();
        entityManager.persist(userMovieRating);
        entityManager.getTransaction().commit();
    }

    public void merge(UserMovieRating userMovieRating) {
        entityManager.getTransaction().begin();
        entityManager.merge(userMovieRating);
        entityManager.getTransaction().commit();
    }
}
