package ru.itis.wr.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.itis.wr.entities.User;
import ru.itis.wr.handlers.ChallengeHandler;
import ru.itis.wr.handlers.ChallengeHandlerFactory;
import ru.itis.wr.services.ChallengeService;

import java.io.IOException;

@WebServlet("/daily/*")
public class DailyChallengeServlet extends HttpServlet {
    private ChallengeHandlerFactory handlerFactory;

    @Override
    public void init() throws ServletException {
        try {
            ChallengeService challengeService = (ChallengeService) getServletContext().getAttribute("challengeService");
            if (challengeService == null) {
                throw new ServletException("ChallengeService not found in ServletContext");
            }
            this.handlerFactory = new ChallengeHandlerFactory(challengeService);
        } catch (Exception e) {
            throw new ServletException("Failed to initialize DailyChallengeServlet", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    private void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path == null) path = "";

        String method = req.getMethod();
        User user = (User) req.getAttribute("currentUser");

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        try {
            ChallengeHandler handler = handlerFactory.getHandler(path, method);

            if (handler != null) {
                handler.handle(req, resp, user);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Handler not found for path: " + path);
            }
        } catch (Exception e) {
            handleError(req, resp, e);
        }
    }

    private void handleError(HttpServletRequest req, HttpServletResponse resp, Exception e)
            throws ServletException, IOException {
        if (isAjaxRequest(req)) {
            sendJsonError(resp, e.getMessage());
        } else {
            req.setAttribute("error", "Failed to load challenge: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(req, resp);
        }
    }

    private boolean isAjaxRequest(HttpServletRequest req) {
        return "XMLHttpRequest".equals(req.getHeader("X-Requested-With")) ||
                "application/json".equals(req.getContentType());
    }

    private void sendJsonError(HttpServletResponse resp, String message) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        resp.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}
