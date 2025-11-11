package ru.itis.wr.filters;

import jakarta.servlet.http.HttpFilter;
import ru.itis.wr.entities.User;
import ru.itis.wr.entities.Role;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/admin/*")
public class AdminFilter extends HttpFilter {
    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        User user = (User) request.getAttribute("currentUser");
        if (user == null || user.getRole() != Role.ADMIN) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admin access required");
            return;
        }

        chain.doFilter(request, response);
    }
}