/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mymovielist.util;

/**
 *
 * @author kiran
 */
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class JwtFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (isPublicPage(httpRequest)) {
            chain.doFilter(request, response);
            return;
        }

        String token = getTokenFromRequest(httpRequest);
        if (token != null && JwtUtil.verifyToken(token)) {
            chain.doFilter(request, response);
        } else {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.xhtml");
        }
    }

    
    private boolean isPublicPage(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (requestURI.equals(contextPath) || requestURI.equals(contextPath + "/")) {
        return true;
    }
        if (requestURI.contains("/javax.faces.resource/")) {
            return true;
        }
        // Pages that do not require authentication
        return requestURI.endsWith("/login.xhtml")
                || requestURI.endsWith("/signup.xhtml")
                || requestURI.endsWith("/availablemovies.xhtml")
                || requestURI.endsWith("/trending.xhtml")
                || requestURI.endsWith("/toprated.xhtml");
    }
    
    private String getTokenFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwtToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}
    
    @Override
    public void destroy() {}
    
}

