package ru.itis.wr.filters;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpFilter;
import ru.itis.wr.entities.User;
import ru.itis.wr.entities.Role;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.itis.wr.services.SecurityService;

import java.io.IOException;

@WebFilter("/admin/*")
public class AdminFilter extends HttpFilter {
    private SecurityService securityService;

    @Override
    public void init() throws ServletException {
        securityService = (SecurityService) getServletContext().getAttribute("securityService");
    }

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String sessionId =  extractSessionId(request);


        User user = null;
        if (sessionId != null) {
            user = securityService.getUser(sessionId);
        }

        if (user == null || user.getRole() != Role.ADMIN) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admin access required");
            return;
        }

        chain.doFilter(request, response);
    }

    private String extractSessionId(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("sessionId".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}