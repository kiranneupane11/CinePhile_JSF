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
import java.util.stream.Collectors;

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
    
    public List<TopRatedMovieDTO> findTopRatedMovies() {
        List<TopRatedMovieDTO> dtos = new ArrayList<>();
        try {
            entityManager.getTransaction().begin();

            List<Object[]> results = entityManager.createQuery(
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

            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            e.printStackTrace();
        }
        return dtos;
    }
    
   public List<TrendingMovieDTO> getTrendingMovies() {
        // Query to get movies, watching count, and average rating
        List<Object[]> results = entityManager.createQuery(
                "SELECT m, COUNT(umr.user) as watchingCount, AVG(umr.rating) as avgRating " +
                "FROM UserMovieRating umr " +
                "JOIN umr.movie m " +
                "WHERE umr.status = 'Watching' " +
                "GROUP BY m " +
                "ORDER BY watchingCount DESC", Object[].class)
                .setMaxResults(10)
                .getResultList();

        List<TrendingMovieDTO> trendingMovies = new ArrayList<>();

        for (Object[] result : results) {
            Movie movie = (Movie) result[0];
            Long watchingCount = (Long) result[1];
            Double avgRating = (Double) result[2]; 

            // Fetch a sample username
            String sampleUsername = entityManager.createQuery(
                    "SELECT u.username " +
                    "FROM UserMovieRating umr " +
                    "JOIN umr.user u " +
                    "WHERE umr.movie = :movie AND umr.status = 'Watching'", String.class)
                    .setParameter("movie", movie)
                    .setMaxResults(1)
                    .getSingleResult();

            TrendingMovieDTO dto = new TrendingMovieDTO(sampleUsername, movie, "Watching", watchingCount, avgRating);
            trendingMovies.add(dto);
        }

        return trendingMovies;
    }

}
