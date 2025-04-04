/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mymovielist.beans;

/**
 *
 * @author kiran
 */

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;
import com.mycompany.mymovielist.service.*;
import javax.annotation.PostConstruct;
import com.mycompany.mymovielist.model.*;
import javax.faces.context.FacesContext;
import javax.faces.application.FacesMessage;
import java.util.Collections;


@Named
@ViewScoped
public class TopRatedBean implements Serializable {
    @Inject
    private MovieService movieService;
    
    private List<TopRatedMovieDTO> topRatedMovies;

    @PostConstruct
    public void init() {
        loadTopRatedMovies();
    }
    
    private void loadTopRatedMovies() {
        try {
            topRatedMovies = movieService.getTopRatedMovies();
            if (topRatedMovies.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning", "No top-rated movies available."));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to load top-rated movies: " + e.getMessage()));
            topRatedMovies = Collections.emptyList();
        }
    }

    // Getter for the view
    public List<TopRatedMovieDTO> getTopRatedMovies() {
        return topRatedMovies;
    }

}
