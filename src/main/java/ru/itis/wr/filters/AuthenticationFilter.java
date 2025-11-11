package ru.itis.wr.filters;

import ru.itis.wr.entities.User;
import ru.itis.wr.services.SecurityService;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class AuthenticationFilter implements Filter {
    private SecurityService securityService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        securityService = (SecurityService) filterConfig.getServletContext().getAttribute("securityService");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        if (path.startsWith("/auth/") || path.startsWith("/css/") ||
                path.startsWith("/js/") || path.startsWith("/images/") ||
                path.equals("/") || path.equals("/index.jsp")) {
            chain.doFilter(request, response);
            return;
        }

        String sessionId = extractSessionId(httpRequest);
        if (sessionId != null) {
            try {
                User user = securityService.getUser(sessionId);
                if (user != null) {
                    httpRequest.setAttribute("currentUser", user);
                    chain.doFilter(request, response);
                    return;
                }
            } catch (Exception e) {
                //сессия невалидна
            }
        }

        if (path.startsWith("/api/") || path.startsWith("/admin/") ||
                path.startsWith("/profile") || path.startsWith("/daily") ||
                path.startsWith("/infinite") || path.startsWith("/shop")) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/auth/login");
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
