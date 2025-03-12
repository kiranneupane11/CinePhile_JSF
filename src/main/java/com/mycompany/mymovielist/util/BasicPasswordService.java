/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mymovielist.util;

/**
 *
 * @author kiran
 */
public class BasicPasswordService implements PasswordService {
    @Override
    public String hashPassword(String plainPassword) {
        return PasswordUtil.hashPassword(plainPassword);
    }

    @Override
    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        return PasswordUtil.verifyPassword(plainPassword, hashedPassword);
    }
}

