/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mymovielist;
import com.mycompany.mymovielist.model.*;
import com.mycompany.mymovielist.service.*;
import com.mycompany.mymovielist.repository.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Optional;
import com.mycompany.mymovielist.util.*;

import org.junit.jupiter.api.Test;

/**
 *
 * @author kiran
 */
public class AuthenticationServiceTest {
    @Test
    public void testSuccessfulLogin() {

        User dummyUser = new User("testUser", "test@example.com", "hashedPassword");

        UserRepository userRepository = mock(UserRepository.class);
        PasswordService passwordService = mock(PasswordService.class);

        when(userRepository.findByUsername(dummyUser.getUsername())).thenReturn(Optional.of(dummyUser));
        when(passwordService.verifyPassword("plainPassword", dummyUser.getPassword())).thenReturn(Boolean.TRUE);

        AuthenticationService authService = new AuthenticationService(userRepository, passwordService);

        Optional<User> result = authService.login("testUser", "plainPassword");

        assertTrue(result.isPresent(), "Login should be successful and return a user.");
        assertEquals(dummyUser, result.get(), "The returned user should match the dummy user.");

    } 
    
    @Test
    public void testSuccesfulSignup(){
        
        User newUser = new User("testUser", "test@example.com", "hashedPassword");
        
        UserRepository userRepository = mock(UserRepository.class);
        PasswordService passwordService = mock(PasswordService.class);
        
        when(userRepository.findByUsername(newUser.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(newUser.getEmail())).thenReturn(Optional.empty());
        
        AuthenticationService authService =  new AuthenticationService(userRepository, passwordService);
        
        Optional<User> result = authService.signup(newUser);
        
        assertTrue(result.isPresent(),"Signup should be successful and return the new user.");
        assertEquals(newUser, result.get());
        
        verify(userRepository, times(1)).add(newUser.getId(), newUser);
        
    }
}