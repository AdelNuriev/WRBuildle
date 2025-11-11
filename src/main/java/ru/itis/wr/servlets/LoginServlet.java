package ru.itis.wr.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.itis.wr.services.SecurityService;

import java.io.IOException;

@WebServlet("/auth/login")
public class LoginServlet extends HttpServlet {
    private SecurityService securityService;

    @Override
    public void init() throws ServletException {
        this.securityService = (SecurityService) getServletContext().getAttribute("securityService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        try {
            String sessionId = securityService.loginUser(email, password);

            Cookie cookie = new Cookie("sessionId", sessionId);
            cookie.setHttpOnly(true);
            cookie.setMaxAge(24 * 60 * 60);
            cookie.setPath("/");
            resp.addCookie(cookie);

            String redirectUrl = req.getContextPath() + "/dashboard";
            resp.sendRedirect(redirectUrl);

        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
        }
    }
}
