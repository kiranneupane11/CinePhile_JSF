/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mymovielist.util;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.*;
/**
 *
 * @author kiran
 */
@ApplicationScoped
public class EMFProvider{
    
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("MovieListPU");

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public static void closeEntityManagerFactory() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}

