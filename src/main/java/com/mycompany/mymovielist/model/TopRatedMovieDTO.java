/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mymovielist.model;
import java.io.Serializable;


/**
 *
 * @author kiran
 */
public class TopRatedMovieDTO implements Serializable {
    private Movie movie;
    private Double avgRating; 

    public TopRatedMovieDTO(Movie movie, Double avgRating) {
        this.movie = movie;
        this.avgRating = avgRating;
    }

    public Movie getMovie() {
        return movie;
    }

    public Double getAvgRating() {
        return Math.round(avgRating * 10) / 10.0;
    }
    
    public int getAvgRatingInt() {
        return (int)Math.round(avgRating);
    }
}

