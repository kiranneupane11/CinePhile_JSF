/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mymovielist.model;
import java.util.*;
import javax.persistence.*;


/**
 *
 * @author kiran
 */

@Entity
@Table(name = "user_playlist", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "name"}))
public class UserPlaylist extends BaseEntity {
    
    @Column(name = "name", nullable = false)
    private String listName;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public UserPlaylist() {
    }
    
    public UserPlaylist(String listName, User user) { 
        this.listName = listName;
        this.user = user;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }
   
    public User getUser(){
        return user;
    }
}