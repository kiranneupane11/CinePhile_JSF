/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mymovielist.api;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.*;
import com.mycompany.mymovielist.filter.CorsFilter;
import com.mycompany.mymovielist.util.JwtFilter;
import com.mycompany.mymovielist.exception.ApiExceptionMapper;

@ApplicationPath("/api")
public class ApplicationConfig extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        return new HashSet<>(Arrays.asList(
        MovieAPI.class,
        AuthAPI.class,
        UserAPI.class,
        CorsFilter.class,
        JwtFilter.class,
        ApiExceptionMapper.class
    ));
    }
}


