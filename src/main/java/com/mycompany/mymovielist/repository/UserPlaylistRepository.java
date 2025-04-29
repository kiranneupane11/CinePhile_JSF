/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mymovielist.repository;

/**
 *
 * @author kiran
 */
import com.mycompany.mymovielist.model.*;
import java.util.*;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

@Named
@ApplicationScoped
public class UserPlaylistRepository extends DatabaseRepository<UserPlaylist, Long> {
    
    public UserPlaylistRepository() {
        super(UserPlaylist.class);
    }
    
    public List<UserPlaylist> getListsByUserId(User user){
        return em.createQuery("SELECT ml FROM UserPlaylist ml WHERE ml.user = :user",
            UserPlaylist.class)
            .setParameter("user", user)
            .setHint("javax.persistence.cache.storeMode", "REFRESH")
            .getResultList();
    }
    
    public Optional<UserPlaylist> getListById (Long playlistId, User user){
        return em.createQuery("SELECT ml FROM UserPlaylist ml WHERE ml.user = :user AND ml.id = :playlistId", UserPlaylist.class)
                .setParameter("playlistId", playlistId)
                .setParameter("user", user)
                .getResultList()
                .stream()
                .findFirst();
    }
    
    public List<UserPlaylist> getListsByUserName(String username){
        return em.createQuery("SELECT ml FROM UserPlaylist ml WHERE ml.user.username = :username",
                UserPlaylist.class)
                .setParameter("username", username)
                .getResultList();
    }
    
    public Optional<UserPlaylist> findByUserIdAndListName(User user, String listName) {
        List<UserPlaylist> results = em.createQuery(
            "SELECT ml FROM UserPlaylist ml WHERE ml.user = :user AND ml.listName = :listName",
            UserPlaylist.class)
            .setParameter("user", user)
            .setParameter("listName", listName)
            .getResultList();
        return results.stream().findFirst();
    }
}
