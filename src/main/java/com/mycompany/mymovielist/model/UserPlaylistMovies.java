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
@Table(name = "user_playlist_movies", uniqueConstraints = @UniqueConstraint(columnNames = {"user_playlist_id", "movie_id"}))
public class UserPlaylistMovies extends BaseEntity {
    
    @ManyToOne
    @JoinColumn(name = "user_playlist_id", nullable = false)
    private UserPlaylist userPlaylist;
    
    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;
    
    public UserPlaylistMovies() {}
    
    public UserPlaylistMovies(UserPlaylist userPlaylist, Movie movie) {
        this.userPlaylist = userPlaylist;
        this.movie = movie;
    }
    
    public UserPlaylist getUserPlaylistId(){
        return userPlaylist;
    }
    
    public Movie getMovieId(){
        return movie;
    }
   
    
}

