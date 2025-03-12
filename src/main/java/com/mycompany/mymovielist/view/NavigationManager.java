/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mymovielist.view;
import com.mycompany.mymovielist.beans.Command;
import java.util.*;
/**
 *
 * @author kiran
 */
public class NavigationManager {
    private final ConsoleIO io;
    private final Map<Integer, Command> commands = new HashMap<>();
    private boolean exit = false;
    
    public NavigationManager(ConsoleIO io) {
        this.io = io;
    }
    
    public void registerCommand(int option, Command command) {
        commands.put(option, command);
    }
    
    public void startNavigation() {
        while (!exit) {
            io.displayMessage("\nWhat would you like to do?");
            io.displayMessage("1. View Available Movies");
            io.displayMessage("2. Add to Watch List");
            io.displayMessage("3. View Your Lists");
            io.displayMessage("4. Log Out");
            
            int choice = io.readInt("Choose an option: ");
            Command command = commands.get(choice);
            if (command != null) {
                command.execute();
            } else {
                io.displayMessage("Invalid choice. Try again.");
            }
        }
    }
    
    public void exit() {
        exit = true;
    }
    
}
