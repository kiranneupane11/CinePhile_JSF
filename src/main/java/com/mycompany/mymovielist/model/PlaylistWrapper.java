/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mymovielist.model;
import java.util.List;
/**
 *
 * @author kiran
 */
public class PlaylistWrapper {
    private UserPlaylist playlist;
    private List<UserMovieRatingDTO> movies;

    public PlaylistWrapper(UserPlaylist playlist, List<UserMovieRatingDTO> movies) {
        this.playlist = playlist;
        this.movies = movies;
    }

    public UserPlaylist getPlaylist() {
        return playlist;
    }

    public List<UserMovieRatingDTO> getMovies() {
        return movies;
    }
}
