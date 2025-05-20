package com.mycompany.mymovielist.filter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class CorsFilter implements ContainerResponseFilter {

    private static final String FRONTEND = "http://localhost:5173";

    @Override
    public void filter(ContainerRequestContext reqCtx,
                       ContainerResponseContext resCtx) {
        // Overwrite (or set) instead of add
        resCtx.getHeaders().putSingle("Access-Control-Allow-Origin", FRONTEND);
        resCtx.getHeaders().putSingle("Access-Control-Allow-Credentials", "true");
        resCtx.getHeaders().putSingle("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resCtx.getHeaders().putSingle("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With, Accept");
        resCtx.getHeaders().putSingle("Access-Control-Max-Age", "86400");   
        resCtx.getHeaders().putSingle("Access-Control-Expose-Headers", "X-Total-Count");
    }
}
