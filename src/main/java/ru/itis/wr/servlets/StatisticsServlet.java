package ru.itis.wr.servlets;

import ru.itis.wr.entities.User;
import ru.itis.wr.services.StatisticsService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/statistics/*")
public class StatisticsServlet extends HttpServlet {
    private StatisticsService statisticsService;

    @Override
    public void init() throws ServletException {
        try {
            statisticsService = (StatisticsService) getServletContext().getAttribute("statisticsService");
            if (statisticsService == null) {
                throw new ServletException("StatisticsService not found in ServletContext");
            }
        } catch (Exception e) {
            throw new ServletException("Failed to initialize StatisticsServlet", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path == null) path = "";

        User user = (User) req.getAttribute("currentUser");

        try {
            switch (path) {
                case "":
                case "/":
                    showUserStatistics(req, resp, user);
                    break;
                case "/leaderboard":
                    showLeaderboard(req, resp);
                    break;
                case "/graph":
                    getStatisticsGraph(req, resp, user);
                    break;
                default:
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            req.setAttribute("error", "Failed to load statistics: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(req, resp);
        }
    }

    private void showUserStatistics(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        var userStats = statisticsService.getUserStatistics(user.getId());
        var recentResults = statisticsService.getRecentUserResults(user.getId(), 30);
        var blockStats = statisticsService.getBlockTypeStatistics(user.getId());

        req.setAttribute("userStats", userStats);
        req.setAttribute("recentResults", recentResults);
        req.setAttribute("blockStats", blockStats);
        req.getRequestDispatcher("/WEB-INF/views/statistics/user.jsp").forward(req, resp);
    }

    private void showLeaderboard(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String period = req.getParameter("period");
        if (period == null) period = "daily";

        var leaderboard = statisticsService.getLeaderboard(period, LocalDate.now());

        req.setAttribute("leaderboard", leaderboard);
        req.setAttribute("period", period);
        req.getRequestDispatcher("/WEB-INF/views/statistics/leaderboard.jsp").forward(req, resp);
    }

    private void getStatisticsGraph(HttpServletRequest req, HttpServletResponse resp, User user)
            throws IOException {
        String blockType = req.getParameter("blockType");
        int days = Integer.parseInt(req.getParameter("days"));

        var graphData = statisticsService.getAttemptsGraphData(user.getId(), blockType, days);
        sendJsonResponse(resp, graphData);
    }

    private void sendJsonResponse(HttpServletResponse resp, Object data) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write("{\"data\": " + data.toString() + "}");
    }
}
