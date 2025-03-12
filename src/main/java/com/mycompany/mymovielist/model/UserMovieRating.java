/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mymovielist.model;

import javax.persistence.*;

/**
 *
 * @author kiran
 */

@Entity
@Table(name = "user_movie_rating")
public class UserMovieRating extends BaseEntity{
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private Status status;

    @Column(nullable = true)
    private Double rating;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;
    
    public enum Status {
        Watched,
        Watching,
        Plan_To_Watch,
        Dropped
    }
    
    public UserMovieRating() {
    }
    
    public UserMovieRating(Movie movieId, Double rating, Status status, User userId){
        this.movie = movieId;
        this.user = userId;
        setStatus(status);
        setRating(rating);
    }          
            
    public Double getRating(){
        return rating;
    }
    
    public void setRating(Double rating) {
        this.rating = rating;
    }
    
     public void setStatus(Status status) {
        this.status = status;
    }
     
     public void setMovieID(Movie movieId){
         this.movie = movieId;
     }
    
    public Status getStatus() {
        return status;
    }
    
    public User getUserID(){
        return user;
    }
    
    public Movie getMovieID(){
        return movie;
    }
}
