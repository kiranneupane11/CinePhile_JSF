/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mymovielist.model;

/**
 *
 * @author kiran
 */
public class TrendingMovieDTO {
    private String username;
    private Movie movie;
    private String status;
    private Long watchingCount;
    private Double avgRating;

    public TrendingMovieDTO(String username, Movie movie, String status,Long watchingCount, Double avgRating) {
        this.username = username;
        this.movie = movie;
        this.status = status;
        this.watchingCount = watchingCount;
        this.avgRating = avgRating;
    }

    // Getters
    public String getUsername() { return username; }
    public Movie getMovie() { return movie; }
    public String getStatus() { return status; }
    public Long getWatchingCount() { return watchingCount; }
    public Double getAvgRating() {
        return Math.round(avgRating * 10) / 10.0;
    }
    
    public int getAvgRatingInt() {
        return avgRating != null ? (int)Math.round(avgRating): 0;
    }
}