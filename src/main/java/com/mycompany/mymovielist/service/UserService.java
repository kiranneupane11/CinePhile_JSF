/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mymovielist.service;
import com.mycompany.mymovielist.model.User;
import com.mycompany.mymovielist.repository.UserRepository;
import javax.inject.Inject;
import java.util.*;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
/**
 *
 * @author kiran
 */
@Named
@ApplicationScoped
public class UserService {
    
    @Inject
    private UserRepository userRepository;
    
     public List<User> getUsersList(){
        return userRepository.getAll();
    }
    
    public void removeUser(User u){
        userRepository.remove(u);
    }
    
     public void updateUser(User u) {
        userRepository.update(u);
    }
}
