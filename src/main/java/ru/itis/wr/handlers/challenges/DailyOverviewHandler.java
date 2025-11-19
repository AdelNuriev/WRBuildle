package ru.itis.wr.handlers.challenges;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.itis.wr.entities.User;
import ru.itis.wr.handlers.BaseHandler;
import ru.itis.wr.services.ChallengeService;

import java.io.IOException;
import java.time.LocalDate;

public class DailyOverviewHandler extends BaseHandler implements ChallengeHandler {
    private final ChallengeService challengeService;

    public DailyOverviewHandler(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    @Override
    public boolean canHandle(String path, String method) {
        return ("".equals(path) || "/".equals(path)) && "GET".equalsIgnoreCase(method);
    }

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        var todayChallenge = challengeService.getTodayChallenge();
        var userResults = challengeService.getUserResultsForDate(user.getId(), LocalDate.now());

        req.setAttribute("challenge", todayChallenge);
        req.setAttribute("userResults", userResults);
        req.getRequestDispatcher("/WEB-INF/views/daily/overview.jsp").forward(req, resp);
    }
}
