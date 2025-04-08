/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mymovielist.repository;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import com.mycompany.mymovielist.model.*;
import com.mycompany.mymovielist.util.EMFProvider;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 *
 * @author kiran
 */
@Named
@ApplicationScoped
public class MovieRepository extends DatabaseRepository<Movie, Long> {
    
    @Inject
    public MovieRepository() {
        super(Movie.class, EMFProvider.getEntityManager());
    }
    
    public List<Movie> findAllOrderByReleaseYearDesc() {
        TypedQuery<Movie> query = entityManager.createQuery(
            "SELECT m FROM Movie m ORDER BY m.releaseYear DESC", Movie.class);
        return query.getResultList();
    }
    
    // NEW: Find movies sorted by release year ascending
    public List<Movie> findAllOrderByReleaseYearAsc() {
        TypedQuery<Movie> query = entityManager.createQuery(
            "SELECT m FROM Movie m ORDER BY m.releaseYear ASC", Movie.class);
        return query.getResultList();
    }
    
    public List<Movie> findByGenre(String genre) {
        TypedQuery<Movie> query = entityManager.createQuery(
            "SELECT m FROM Movie m WHERE LOWER(m.genre) LIKE :genre", Movie.class);
        query.setParameter("genre", "%" + genre.toLowerCase() + "%");
        return query.getResultList();
    }
    
}
