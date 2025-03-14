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
    
}
