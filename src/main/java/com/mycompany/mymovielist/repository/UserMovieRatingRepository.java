/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mymovielist.repository;
import com.mycompany.mymovielist.model.*;
import javax.persistence.EntityManager;
import java.util.*;

/**
 *
 * @author kiran
 */
public class UserMovieRatingRepository extends DatabaseRepository<UserMovieRating, Long> {
    public UserMovieRatingRepository(EntityManager entityManager) {
        super(UserMovieRating.class, entityManager);
    }
    
    public List<Object[]> getUserRatedMovies(UserMovieRating usermovie){
    return entityManager.createQuery(
        "SELECT um,m FROM UserMovieRating um JOIN Movie m ON um.movieId = m.id WHERE um.userId = :userId", 
        Object[].class)
        .setParameter("userId", usermovie.getUserID())
        .getResultList();
    }
}
