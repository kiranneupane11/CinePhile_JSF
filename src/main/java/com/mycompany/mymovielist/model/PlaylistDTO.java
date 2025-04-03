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
public class PlaylistDTO {
    private Long playlistId;
    private String listName;
    private List<UserMovieRatingDTO> movies;

    public PlaylistDTO(Long playlistId, String listName, List<UserMovieRatingDTO> movies) {
        this.playlistId = playlistId;
        this.listName = listName;
        this.movies = movies;
    }

    public Long getPlaylistId() {
        return playlistId;
    }

    public String getListName() {
        return listName;
    }

    public List<UserMovieRatingDTO> getMovies() {
        return movies;
    }
    
}
