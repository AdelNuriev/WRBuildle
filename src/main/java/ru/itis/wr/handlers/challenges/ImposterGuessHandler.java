package ru.itis.wr.handlers.challenges;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.itis.wr.entities.User;
import ru.itis.wr.handlers.BaseHandler;
import ru.itis.wr.helper.GuessResult;
import ru.itis.wr.services.ChallengeService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class ImposterGuessHandler extends BaseHandler implements ChallengeHandler {
    private final ChallengeService challengeService;

    public ImposterGuessHandler(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    @Override
    public boolean canHandle(String path, String method) {
        return "/guess/imposter".equals(path) && "POST".equalsIgnoreCase(method);
    }

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse resp, User user) throws IOException {
        try {
            Long itemId = Long.parseLong(req.getParameter("itemId"));

            GuessResult result = challengeService.processImposterGuess(user.getId(), itemId, LocalDate.now());
            user.earnCoins(result);
            Map<String, Object> response = getStringObjectMap(result);

            sendJsonResponse(resp, response);
        } catch (Exception e) {
            sendJsonError(resp, e.getMessage());
        }
    }

    private static Map<String, Object> getStringObjectMap(GuessResult result) {
        Map<String, Object> response = new HashMap<>();
        response.put("correct", result.isCorrect());
        response.put("message", result.getMessage());
        response.put("score", result.getScoreEarned());
        response.put("completed", result.getUserResult() != null ? result.getUserResult().getCompleted() : false);

        if (result.getAdditionalData() instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> additionalData = (Map<String, Object>) result.getAdditionalData();
            response.putAll(additionalData);
        }
        return response;
    }
}
