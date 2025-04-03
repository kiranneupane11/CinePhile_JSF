/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mymovielist.model;

import java.util.*;
import javax.persistence.*;
import com.mycompany.mymovielist.util.*;


/**
 *
 * @author kiran
 */

@Entity
@Table(name = "user")
public class User extends BaseEntity {
    
    @Column(unique = true, nullable = false)
    protected String username;
    
    @Column(unique = true, nullable = false)
    protected String email;
    
    @Column(nullable = false)
    protected String password;

    public User() {} 
    
    public User(String username, String email, String rawPassword) {
        this.username = username;
        this.email = email;
        this.password = PasswordUtil.hashPassword(rawPassword);
    }

    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username){
        this.username = username;
    }
    
    public boolean checkPassword(String rawPassword, PasswordService passwordService) {
        return passwordService.verifyPassword(rawPassword, this.password);
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
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return this.getId() != null && this.getId().equals(user.getId());
    }


    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
