/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mymovielist.beans;

import com.mycompany.mymovielist.model.*;
import com.mycompany.mymovielist.service.PlaylistService;
import com.mycompany.mymovielist.view.ConsoleIO;
import java.util.*;
/**
 *
 * @author kiran
 */
public class BrowseListsCommand implements Command{
    private final ConsoleIO io;
    private final PlaylistService playlistService;
    private final User user;
    
    public BrowseListsCommand(ConsoleIO io,PlaylistService playlistService, User user ){
        this.io = io;
        this.playlistService = playlistService;
        this.user = user;
    }
    
    @Override
    public void execute(){
        String username = io.readString("Enter username to view their movie lists");
        List<UserPlaylist> lists = playlistService.browseLists(username);
        
            for(UserPlaylist list : lists){
                io.displayMessage(list.getId() + ". " + list.getListName());
            }
        
    }
}
