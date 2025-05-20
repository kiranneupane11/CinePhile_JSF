///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.mycompany.mymovielist.util;
//
///**
// *
// * @author kiran
// */
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import javax.ws.rs.ext.ContextResolver;
//import javax.ws.rs.ext.Provider;
//
///**
// * Provides a customized ObjectMapper for JAX-RS to handle Java Time API types like Year.
// *
// * @author kiran
// */
//@Provider
//public class ObjectMapperContextResolver implements ContextResolver<ObjectMapper> {
//    private final ObjectMapper mapper;
//
//    public ObjectMapperContextResolver() {
//        mapper = new ObjectMapper();
//        mapper.registerModule(new JavaTimeModule()); 
//    }
//
//    @Override
//    public ObjectMapper getContext(Class<?> type) {
//        return mapper;
//    }
//}
