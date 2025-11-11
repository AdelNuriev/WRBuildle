package ru.itis.wr.handlers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.itis.wr.entities.User;
import ru.itis.wr.services.ChallengeService;

import java.io.IOException;
import java.time.LocalDate;

public class MissingGuessHandler extends BaseChallengeHandler implements ChallengeHandler {
    private final ChallengeService challengeService;

    public MissingGuessHandler(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    @Override
    public boolean canHandle(String path, String method) {
        return "/guess/missing".equals(path) && "POST".equalsIgnoreCase(method);
    }

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse resp, User user) throws IOException {
        try {
            Long itemId = Long.parseLong(req.getParameter("itemId"));

            var result = challengeService.processMissingGuess(user.getId(), itemId, LocalDate.now());
            sendJsonResponse(resp, result);
        } catch (Exception e) {
            sendJsonError(resp, e.getMessage());
        }
    }
}
