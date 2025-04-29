/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mymovielist.repository;
import com.mycompany.mymovielist.model.*;
import java.util.*;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.persistence.TypedQuery;

/**
 *
 * @author kiran
 */
@Named
@ApplicationScoped
public class UserMovieRatingRepository extends DatabaseRepository<UserMovieRating, Long> {
    
    public UserMovieRatingRepository() {
        super(UserMovieRating.class);
    }
    
    public List<Object[]> getUserRatedMovies(UserMovieRating usermovie){
    return em.createQuery(
        "SELECT um,m FROM UserMovieRating um JOIN Movie m ON um.movie = m.id WHERE um.user = :userId", 
        Object[].class)
        .setParameter("userId", usermovie.getUserID())
        .getResultList();
    }
    
    public Optional<UserMovieRating> findByUserAndMovie(User user, Movie movie) {
        TypedQuery<UserMovieRating> query = em.createQuery(
            "SELECT umr FROM UserMovieRating umr WHERE umr.user = :user AND umr.movie = :movie",
            UserMovieRating.class);
        query.setParameter("user", user);
        query.setParameter("movie", movie);
        return Optional.ofNullable(query.getSingleResult());
    }
        
    public List<TopRatedMovieDTO> findTopRatedMovies() {
        List<TopRatedMovieDTO> dtos = new ArrayList<>();
            List<Object[]> results = em.createQuery(
                "SELECT umr.movie, AVG(umr.rating) as avgRating " +
                "FROM UserMovieRating umr " +
                "GROUP BY umr.movie " +
                "HAVING COUNT(umr.rating) > 0 " +
                "ORDER BY AVG(umr.rating) DESC", Object[].class)
                .setMaxResults(10)
                .getResultList();

            for (Object[] row : results) {
                Movie m = (Movie) row[0];
                Double avg = (Double) row[1];
                dtos.add(new TopRatedMovieDTO(m, avg));
            }
        return dtos;
    }
    
  public List<TrendingMovieDTO> getTrendingMovies() {
    List<Object[]> results = em.createQuery(
        "SELECT m, COUNT(umr.user) as watchingCount, AVG(umr.rating) as avgRating, " +
        "       (SELECT MAX(u.username) " +  
        "        FROM UserMovieRating umr2 " +
        "        JOIN umr2.user u " +
        "        WHERE umr2.movie = m AND umr2.status = 'Watching') as sampleUsername " +
        "FROM UserMovieRating umr " +
        "JOIN umr.movie m " +
        "WHERE umr.status = 'Watching' " +
        "GROUP BY m " +
        "ORDER BY COUNT(umr.user) DESC", Object[].class)
        .setMaxResults(10)
        .getResultList();

    List<TrendingMovieDTO> trendingMovies = new ArrayList<>();

    for (Object[] result : results) {
        Movie movie = (Movie) result[0];
        Long watchingCount = (Long) result[1];
        Double avgRating = (Double) result[2];
        String sampleUsername = (String) result[3];

        trendingMovies.add(new TrendingMovieDTO(
            sampleUsername != null ? sampleUsername : "N/A",
            movie, 
            "Watching", 
            watchingCount, 
            avgRating
        ));
        }   
    return trendingMovies;
    }

}
