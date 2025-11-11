package ru.itis.wr.servlets;

import ru.itis.wr.entities.User;
import ru.itis.wr.services.ChallengeService;
import ru.itis.wr.services.StatisticsService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

@WebServlet({"/dashboard", "/", ""})
public class DashboardServlet extends HttpServlet {
    private ChallengeService challengeService;
    private StatisticsService statisticsService;

    @Override
    public void init() throws ServletException {
        try {
            challengeService = (ChallengeService) getServletContext().getAttribute("challengeService");
            statisticsService = (StatisticsService) getServletContext().getAttribute("statisticsService");

            if (challengeService == null) {
                throw new ServletException("ChallengeService not found in ServletContext");
            }
            if (statisticsService == null) {
                throw new ServletException("StatisticsService not found in ServletContext");
            }
        } catch (Exception e) {
            throw new ServletException("Failed to initialize DashboardServlet", e);
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
            var todayChallenge = challengeService.getTodayChallenge();
            var userStats = statisticsService.getUserStatistics(user.getId());
            var todayResults = challengeService.getUserResultsForDate(user.getId(), LocalDate.now());

            req.setAttribute("todayChallenge", todayChallenge);
            req.setAttribute("userStats", userStats);
            req.setAttribute("todayResults", todayResults);
            req.setAttribute("completedBlocks", countCompletedBlocks(todayResults));

            req.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Failed to load dashboard: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(req, resp);
        }
    }

    private int countCompletedBlocks(java.util.List<?> results) {
        return (int) results.stream()
                .filter(result -> {
                    try {
                        var completed = result.getClass().getMethod("getCompleted").invoke(result);
                        return Boolean.TRUE.equals(completed);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .count();
    }
}
