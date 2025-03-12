/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mymovielist.beans;
import com.mycompany.mymovielist.view.*;

/**
 *
 * @author kiran
 */
public class LogoutCommand implements Command {
    private final UI ui;
    private final NavigationManager navManager;
    
    public LogoutCommand(UI ui, NavigationManager navManager) {
        this.ui = ui;
        this.navManager = navManager;
    }
    
    @Override
    public void execute() {
        navManager.exit();
        ui.logout();
    }
}
