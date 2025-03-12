/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mymovielist.repository;
import java.util.*;
import java.io.Serializable;


/**
 *
 * @author kiran
 */

public interface BaseRepository<T, ID> extends Serializable{
    void add(ID id, T item);
    void remove(ID id);
    Optional<T> get(ID id);
    List<T> getAll();
}


