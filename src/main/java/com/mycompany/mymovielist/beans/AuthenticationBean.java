/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mymovielist.beans;
import com.mycompany.mymovielist.service.AuthenticationService;
import com.mycompany.mymovielist.model.User;
import com.mycompany.mymovielist.util.PasswordUtil;
import java.util.Optional;
import javax.enterprise.context.SessionScoped; 
import javax.inject.Inject;
import javax.inject.Named;
import javax.faces.context.FacesContext;
import java.io.Serializable;


/**
 *
 * @author kiran
 */
@Named
@SessionScoped
public class AuthenticationBean implements Serializable {
    
    @Inject
    private AuthenticationService authService;
    
    private String username;
    private String email;
    private String usernameOrEmail;
    private String password;
    private User loggedInUser;
    private String message;

    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }

    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    public void setUsername(String username){
        this.username = username;
    }
    
    public String getUsername() {
        return loggedInUser != null ? loggedInUser.getUsername() : "";
    }
    
    public boolean isLoggedIn() {
        return loggedInUser != null;
    }
    
    public String login(){
        Optional<User> userOpt = authService.login(usernameOrEmail, password);
            if(userOpt.isPresent()){
                loggedInUser = userOpt.get();
                message = "Login Successful!";
            } else{
                message = "Invalid username or password.";
            }
        // Put the message into flash scope so it survives the redirect
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("message", message);

    return userOpt.isPresent() ? "loginSuccess" : "loginFailure";
    }
    
    public String logout() {
        loggedInUser = null;
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "login?faces-redirect=true";
    }
    
    public String signup(){
        
        if (username == null || email == null || password == null || username.trim().isEmpty() || email.trim().isEmpty() || password.trim().isEmpty()) {
            message = "All fields are required";
            FacesContext.getCurrentInstance().getExternalContext().getFlash().put("message", message);
        return "signup?faces-redirect=true";
        }
        
        User newUser = new User();
        newUser.setUsername(this.username);
        newUser.setEmail(this.email);
        newUser.setPassword(this.password = PasswordUtil.hashPassword(this.password));
        
        Optional<User> userOpt = authService.signup(newUser);
            if(!userOpt.isPresent()){
                message = "User already exists";
                return "signup?faces-redirect=true";
            } else{
                message = "Sign Up Successful!!!";
                FacesContext.getCurrentInstance().getExternalContext().getFlash().put("message", message);
                return "login?faces-redirect=true";
            }
    }
    
}
