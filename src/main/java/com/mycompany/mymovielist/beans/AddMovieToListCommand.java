///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.mycompany.mymovielist.beans;
//import com.mycompany.mymovielist.view.*;
//import com.mycompany.mymovielist.model.*;
//import com.mycompany.mymovielist.service.*;
//import java.util.*;
///**
// *
// * @author kiran
// */
//public class AddMovieToListCommand implements Command{
//    private final ConsoleIO io;
//    private final MovieService movieService;
//    private final PlaylistService playlistService;
//    private final User loggedInUser;
//    
//    public AddMovieToListCommand(ConsoleIO io, MovieService movieService, PlaylistService playlistService, User loggedInUser) {
//        this.io = io;
//        this.movieService = movieService;
//        this.playlistService = playlistService;
//        this.loggedInUser = loggedInUser;
//    }
//    
//    @Override
//    public void execute() {
//        List<Movie> movies = movieService.getAvailableMovies();
//        if (movies.isEmpty()) {
//            io.displayMessage("No movies available.");
//            return;
//        }
//        for (Movie movie : movies) {
//            io.displayMessage(movie.getId() + ". " + movie.getTitle());
//        }
//        long Id = io.readLong("Enter movie ID: ");
//        Optional<Movie> movieOpt = movieService.getMovieById(Id);
//        
//        if (!movieOpt.isPresent()) {
//            io.displayMessage("Invalid movie ID.");
//            return;
//        }
//        
//        Double rating = null;
//        while (true) {
//            String ratingInput = io.readString("Enter your rating (1-10) or press Enter to skip: ").trim();
//            if (!ratingInput.isEmpty()) {
//                break; 
//            }
//            try {
//                rating = Double.parseDouble(ratingInput);
//                if (rating < 1 || rating > 10) {
//                    io.displayMessage("Rating must be between 1 and 10.");
//                    continue;
//                }
//                break;
//            } catch (NumberFormatException e) {
//                io.displayMessage("Invalid rating input. Please enter a number between 1 and 10.");
//            }
//        }
//
//        io.displayMessage("Select status:");
//        UserMovieRating.Status[] statuses = UserMovieRating.Status.values();
//        for (int i = 0; i < statuses.length; i++) {
//            io.displayMessage((i + 1) + ". " + statuses[i]);
//        }
//
//        UserMovieRating.Status status = null;
//        while (status == null) {
//            int statusChoice = io.readInt("Enter choice: ");
//            if (statusChoice < 1 || statusChoice > statuses.length) {
//                io.displayMessage("Invalid choice. Please select a valid status.");
//            } else {
//                status = statuses[statusChoice - 1];
//            }
//        }
//        
//        UserMovieRating userMovieRating = new UserMovieRating(movieOpt.get(),rating, status, loggedInUser);
//        String listName = io.readString("Enter List Name to add the movie");
//        UserPlaylist newMovieList = new UserPlaylist(listName, loggedInUser);
//        
//        if (playlistService.addMovieToList(loggedInUser, userMovieRating, newMovieList)) {
//            io.displayMessage("Movie added successfully!");
//        } else {
//            io.displayMessage("Invalid movie ID.");
//        }
//    }
//}
