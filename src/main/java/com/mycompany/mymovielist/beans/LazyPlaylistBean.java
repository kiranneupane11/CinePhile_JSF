///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.mycompany.mymovielist.beans;
//
///**
// *
// * @author kiran
// */
//import java.io.Serializable;
//import java.util.List;
//import java.util.Optional;
//import javax.faces.view.ViewScoped;
//import javax.inject.Inject;
//import javax.inject.Named;
//import org.primefaces.event.TabChangeEvent;
//import com.mycompany.mymovielist.model.*;
//import com.mycompany.mymovielist.service.*;
//import javax.annotation.PostConstruct;
//
//
//@Named
//@ViewScoped
//public class LazyPlaylistBean implements Serializable {
//
//   private static final long serialVersionUID = 1L;
//    
//    private List<PlaylistDTO> playlists;
//    
//    @Inject
//    private PlaylistService playlistService;
//    
//    @Inject
//    private AuthenticationBean currentUser;  
//    
//    @PostConstruct
//    public void init() {
//        playlists = playlistService.loadPlaylistsMetadata(currentUser.getLoggedInUser());
//    }
//    
//    public void onTabChange(TabChangeEvent event) {
//        Object attr = event.getTab().getAttributes().get("playlistId");
//        if (attr != null) {
//            final Long playlistId;
//            try {
//                playlistId = Long.valueOf(attr.toString());
//            } catch (NumberFormatException e) {
//                e.printStackTrace();
//                return;
//            }
//                // Find the corresponding PlaylistDTO in our list
//                Optional<PlaylistDTO> dtoOpt = playlists.stream()
//                        .filter(dto -> dto.getPlaylistId().equals(playlistId))
//                        .findFirst();
//                
//                if (dtoOpt.isPresent()) {
//                    PlaylistDTO dto = dtoOpt.get();
//                    // Only load movies if not already loaded
//                    if (dto.getMovies() == null || dto.getMovies().isEmpty()) {
//                        Optional<UserPlaylist> plOpt = playlistService.getPlaylist(playlistId, currentUser.getLoggedInUser());
//                        if (plOpt.isPresent()) {
//                            dto.setMovies(playlistService.viewMoviesFromPlaylistLazy(plOpt.get(), currentUser.getLoggedInUser()));
//                        }
//                    }
//                }
//            }
//        }
//    
//    // Getters and Setters
//    public List<PlaylistDTO> getPlaylists() {
//        return playlists;
//    }
//    
//    public void setPlaylists(List<PlaylistDTO> playlists) {
//        this.playlists = playlists;
//    }
//}
//
