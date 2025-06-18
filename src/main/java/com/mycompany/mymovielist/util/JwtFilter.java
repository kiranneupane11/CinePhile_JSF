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
import com.mycompany.mymovielist.model.Role;

@WebFilter("/*")
public class JwtFilter implements Filter {
    
    private static final String API_PREFIX = "/api/";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest  httpReq  = (HttpServletRequest)  request;
        HttpServletResponse httpResp = (HttpServletResponse) response;

        String uri         = httpReq.getRequestURI();
        String contextPath = httpReq.getContextPath();
        
  
        if (uri.startsWith(contextPath + "/api/auth/")) {
            chain.doFilter(request, response);
            return;
        }
        
        if ("OPTIONS".equalsIgnoreCase(httpReq.getMethod())) {
            return;
        }

        // 1) If it's a JSF resource or a public JSF page, let it through:
        if (isPublicPage(uri, contextPath) || uri.contains("/javax.faces.resource/")) {
            chain.doFilter(request, response);
            return;
        }

        // 2) If it's under /api/, enforce JWT but return 401 on failure:
        if (uri.startsWith(contextPath + API_PREFIX)) {
            String token = getTokenFromRequest(httpReq);
            if (token != null && JwtUtil.verifyToken(token)) {
                chain.doFilter(request, response);
            } else {
                httpResp.setContentType("application/json");
                httpResp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpResp.getWriter()
                      .write("{\"error\":\"Unauthorized - invalid or missing token\"}");
            }
            return;
        }

        // 3) Otherwise (normal JSF pages), do your redirect-based login:
        String token = getTokenFromRequest(httpReq);
        if (token != null && JwtUtil.verifyToken(token)) {
            Role role = JwtUtil.getRole(token);

            // ✅ Redirect admin to dbmovies.xhtml
            if (role == Role.ADMIN && isPublicPage(uri, contextPath)) {
                httpResp.sendRedirect(contextPath + "/dbmovies.xhtml");
                return;
            }

            chain.doFilter(request, response);
        } else {
            httpResp.sendRedirect(contextPath + "/login.xhtml");
        }
    }


    
    private boolean isPublicPage(String requestURI, String contextPath) {
        // home
        if (requestURI.equals(contextPath) || requestURI.equals(contextPath + "/")) {
            return true;
        }
        // pages that don't require login
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
                if ("accessToken".equals(cookie.getName())) {
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

