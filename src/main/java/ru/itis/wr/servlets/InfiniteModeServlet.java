package ru.itis.wr.servlets;

import ru.itis.wr.entities.User;
import ru.itis.wr.services.ChallengeService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/infinite/*")
public class InfiniteModeServlet extends HttpServlet {
    private ChallengeService challengeService;

    @Override
    public void init() throws ServletException {
        try {
            challengeService = (ChallengeService) getServletContext().getAttribute("challengeService");
            if (challengeService == null) {
                throw new ServletException("ChallengeService not found in ServletContext");
            }
        } catch (Exception e) {
            throw new ServletException("Failed to initialize InfiniteModeServlet", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getAttribute("currentUser");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        try {
            var currentGame = challengeService.getCurrentInfiniteGame(user.getId());
            req.setAttribute("currentGame", currentGame);
            req.getRequestDispatcher("/WEB-INF/views/infinite/game.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Failed to load infinite mode: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/infinite/game.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        User user = (User) req.getAttribute("currentUser");

        try {
            if ("/start".equals(path)) {
                var game = challengeService.startInfiniteGame(user.getId());
                req.setAttribute("currentGame", game);
                req.setAttribute("success", "Game started!");
                req.getRequestDispatcher("/WEB-INF/views/infinite/game.jsp").forward(req, resp);
            } else if ("/guess".equals(path)) {
                Long itemId = Long.parseLong(req.getParameter("itemId"));
                var result = challengeService.processInfiniteGuess(user.getId(), itemId);
                req.setAttribute("guessResult", result);
                req.getRequestDispatcher("/WEB-INF/views/infinite/game.jsp").forward(req, resp);
            } else if ("/hint".equals(path)) {
                var hint = challengeService.getInfiniteHint(user.getId());
                req.setAttribute("hint", hint);
                req.getRequestDispatcher("/WEB-INF/views/infinite/game.jsp").forward(req, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/infinite/game.jsp").forward(req, resp);
        }
    }
}
