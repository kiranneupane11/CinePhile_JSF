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
import com.mycompany.mymovielist.util.EMFProvider;
import javax.persistence.EntityManager;
import java.util.*;


public class UserPlaylistRepository extends DatabaseRepository<UserPlaylist, Long> {
    public UserPlaylistRepository() {
        super(UserPlaylist.class, EMFProvider.getEntityManager());
    }
    
    public List<UserPlaylist> getListsByUserId(User user){
        return entityManager.createQuery("SELECT ml FROM UserPlaylist ml WHERE ml.user = :user", UserPlaylist.class)
            .setParameter("user", user)
            .getResultList();
    }
    
    public List<UserPlaylist> getListsByUserName(String username){
        return entityManager.createQuery("SELECT ml FROM UserPlaylist ml WHERE ml.user.username = :username", UserPlaylist.class)
                .setParameter("username", username)
                .getResultList();
    }
    
    public Optional<UserPlaylist> findByUserIdAndListName(UserPlaylist movieList) {
    return entityManager.createQuery(
        "SELECT ml FROM UserPlaylist ml WHERE ml.user = :userId AND ml.listName = :listName",
        UserPlaylist.class)
        .setParameter("userId", movieList.getUser())
        .setParameter("listName", movieList.getListName())
        .getResultStream()
        .findFirst();
    }
}
