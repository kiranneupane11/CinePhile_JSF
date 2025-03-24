///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.mycompany.mymovielist.beans;
//import com.mycompany.mymovielist.view.*;
//import com.mycompany.mymovielist.model.*;
//import com.mycompany.mymovielist.service.PlaylistService;
//import java.util.*;
//
///**
// *
// * @author kiran
// */
//public class ViewMovieListsCommand implements Command {
//    private final ConsoleIO io;
//    private final PlaylistService playlistService;
//    private final User loggedInUser;
//    
//    public ViewMovieListsCommand(ConsoleIO io, PlaylistService playlistService, User loggedInUser) {
//        this.io = io;
//        this.playlistService = playlistService;
//        this.loggedInUser = loggedInUser;
//    }
//    
//    @Override
//    public void execute() {
//        List<UserPlaylist> lists = playlistService.getUserLists(loggedInUser);
//        if (lists.isEmpty()) {
//            io.displayMessage("No movie lists found.");
//            return;
//        }
//        for (UserPlaylist list : lists) {
//            io.displayMessage(list.getId() + ". " + list.getListName());
//        }
//        
//        long playlistId = io.readLong("Enter List ID to view it: ");
//        
//        Optional<UserPlaylist> playlistOpt = playlistService.getUserPlaylistById(playlistId, loggedInUser);
//        if (!playlistOpt.isPresent()) {
//            io.displayMessage("Invalid List ID.");
//            return;
//        }
//        UserPlaylist playlist = playlistOpt.get();
//        List<UserMovieRatingDTO> movies = playlistService.viewList(playlist, loggedInUser);
//        
//        if (movies.isEmpty()) {
//            io.displayMessage("No movies found in this list.");
//            return;
//        }
//        
//        for (UserMovieRatingDTO movie : movies) {
//            io.displayMessage("ID: " + movie.getMovieID() +
//                          " | Title: " + movie.getTitle() +
//                          " | Year: " + movie.getReleaseYear() +
//                          " | Genre: " + movie.getGenre() +
//                          " | Rating: " + movie.getRating() +
//                          " | Status: " + movie.getStatus()+
//                          " | Description: " + movie.getDescription());
//        }
//    }
//}
