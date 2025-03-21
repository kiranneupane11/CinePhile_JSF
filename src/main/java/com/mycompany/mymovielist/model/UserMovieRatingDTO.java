/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mymovielist.model;
import java.time.Year; 


/**
 *
 * @author kiran
 */
public class UserMovieRatingDTO {
    private String title;
    private String genre;
    private Double rating;
    private UserMovieRating.Status status;
    private Long movieID;
    private Year releaseYear;
    private String description;
    

    public UserMovieRatingDTO(Movie movie, UserMovieRating userMovie) {
        this.movieID = movie.getId();
        this.title = movie.getTitle();
        this.releaseYear = movie.getReleaseYear();
        this.genre = movie.getGenre();
        this.rating = userMovie.getRating();
        this.status = userMovie.getStatus();
        this.description = movie.getDescription();
    }

    public Long getMovieID() { return movieID; }
    public String getTitle() { return title; }
    public String getGenre() { return genre; }
    public Double getRating() { return (rating != null) ? rating : 0; }
    public UserMovieRating.Status getStatus() { return status; }
    public Year getReleaseYear() {return releaseYear; }
    public String getDescription(){return description; }
    public int getRatingAsInt() {return (int) Math.round(rating);}
}