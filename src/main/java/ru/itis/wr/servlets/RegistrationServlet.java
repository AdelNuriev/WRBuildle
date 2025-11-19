package ru.itis.wr.servlets;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.itis.wr.exceptions.AuthenticationException;
import ru.itis.wr.services.SecurityService;

import java.io.IOException;

@WebServlet("/auth/register")
public class RegistrationServlet extends HttpServlet {
    SecurityService securityService;

    @Override
    public void init() throws ServletException {
        this.securityService = (SecurityService) getServletContext().getAttribute("securityService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String passwordRepeat = req.getParameter("passwordRepeat");

        String sessionId = "";
        try {
            sessionId = securityService.registerUser(username, email, password, passwordRepeat);
        } catch (AuthenticationException e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
            return;
        }

        Cookie cookie = new Cookie("sessionId", sessionId);
        cookie.setMaxAge(1000*60);
        resp.addCookie(cookie);
        resp.sendRedirect(req.getContextPath() + "/auth/login");
    }
}
