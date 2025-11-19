package ru.itis.wr.servlets;

import ru.itis.wr.entities.User;
import ru.itis.wr.handlers.challenges.ChallengeHandler;
import ru.itis.wr.handlers.infinite.InfiniteHandlerFactory;
import ru.itis.wr.services.ChallengeService;
import ru.itis.wr.services.ItemService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/infinite/*")
public class InfiniteModeServlet extends HttpServlet {
    private InfiniteHandlerFactory handlerFactory;

    @Override
    public void init() throws ServletException {
        try {
            ChallengeService challengeService = (ChallengeService) getServletContext().getAttribute("challengeService");
            ItemService itemService = (ItemService) getServletContext().getAttribute("itemService");

            if (challengeService == null) {
                throw new ServletException("ChallengeService not found in ServletContext");
            }
            if (itemService == null) {
                throw new ServletException("ItemService not found in ServletContext");
            }

            this.handlerFactory = new InfiniteHandlerFactory(challengeService, itemService);
        } catch (Exception e) {
            throw new ServletException("Failed to initialize InfiniteModeServlet", e);
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
        req.setAttribute("error", "Failed to load infinite mode: " + e.getMessage());
        req.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(req, resp);
    }
}
