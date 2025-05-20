/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mymovielist.exception;

/**
 *
 * @author kiran
 */
public class ApiException extends RuntimeException {
    private final int status;
    private final String message;

    public ApiException(int status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }
    public int getStatus() { return status; }
    public String getMessage() { return message; }
}

