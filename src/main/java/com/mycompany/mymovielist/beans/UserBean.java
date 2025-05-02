/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mymovielist.beans;

import com.mycompany.mymovielist.model.User;
import com.mycompany.mymovielist.service.UserService;
import java.io.Serializable;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.annotation.PostConstruct;

/**
 *
 * @author kiran
 */
@Named
@ViewScoped
public class UserBean implements Serializable{
    
    @Inject
    private UserService userService;
    
    private List<User> allUsers;
    private User userToRemove;
    private User selectedUser;
    
    @PostConstruct
    public void init() {
        allUsers = userService.getUsersList();
    }
    
     public List<User> getAllUsers(){
        return allUsers;
    }
    
    public void openEdit(User u) {
        this.selectedUser = u;
    }
    
    public void saveUser() {
        try {
            userService.updateUser(selectedUser);
            FacesContext.getCurrentInstance().addMessage(null,
              new FacesMessage(FacesMessage.SEVERITY_INFO,
                               "Saved",
                               "Role for \"" + selectedUser.getUsername() + "\" updated."));
            allUsers = userService.getUsersList();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
              new FacesMessage(FacesMessage.SEVERITY_ERROR,
                               "Save failed",
                               e.getMessage()));
        } finally {
            selectedUser = null;
        }
    }
     
     public void prepareDelete(User u) {
        this.userToRemove = u;
    }

    public void confirmDelete() {
        if (userToRemove != null) {
            onDeleteUser(userToRemove);
            userToRemove = null;
        }
    }
    
    public void onDeleteUser(User user) {
        try {
            userService.removeUser(user);
            FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                                 "Deleted",
                                 "User \"" + user.getUsername() + "\" has been removed."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                 "Delete Failed",
                                 e.getMessage()));
        }
        allUsers = userService.getUsersList();
        userToRemove = null;
    }
    
    public User getUserToRemove() { return userToRemove; }
    public void setUserToRemove(User u) { this.userToRemove = u; }

    public User getSelectedUser() { return selectedUser; }
    public void setSelectedUser(User u) { this.selectedUser = u; }
    
}
