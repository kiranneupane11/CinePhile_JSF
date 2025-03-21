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
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@ApplicationScoped
public class UserPlaylistRepository extends DatabaseRepository<UserPlaylist, Long> {
    @Inject
    public UserPlaylistRepository() {
        super(UserPlaylist.class, EMFProvider.getEntityManager());
    }
    
    public List<UserPlaylist> getListsByUserId(User user){
        return entityManager.createQuery("SELECT ml FROM UserPlaylist ml WHERE ml.user = :user",
            UserPlaylist.class)
            .setParameter("user", user)
            .getResultList();
    }
    
    public List<UserPlaylist> getListsByUserName(String username){
        return entityManager.createQuery("SELECT ml FROM UserPlaylist ml WHERE ml.user.username = :username",
                UserPlaylist.class)
                .setParameter("username", username)
                .getResultList();
    }
    
    public Optional<UserPlaylist> findByUserIdAndListName(User user, String listName) {
        List<UserPlaylist> results = entityManager.createQuery(
            "SELECT ml FROM UserPlaylist ml WHERE ml.user = :user AND ml.listName = :listName",
            UserPlaylist.class)
            .setParameter("user", user)
            .setParameter("listName", listName)
            .getResultList();
        return results.stream().findFirst();
    }
}
