package ru.itis.wr.handlers.infinite;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.itis.wr.entities.User;
import ru.itis.wr.handlers.BaseHandler;
import ru.itis.wr.handlers.challenges.ChallengeHandler;
import ru.itis.wr.services.ChallengeService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class InfiniteStartHandler extends BaseHandler implements ChallengeHandler {
    private final ChallengeService challengeService;

    public InfiniteStartHandler(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    @Override
    public boolean canHandle(String path, String method) {
        return "/start/infinite".equals(path) && "POST".equalsIgnoreCase(method);
    }

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse resp, User user) throws IOException {
        try {
            var game = challengeService.startInfiniteGame(user.getId());
            Map<String, Object> response = new HashMap<>();

            response.put("success", true);
            response.put("message", "Новая игра начата");
            response.put("game", game);
            response.put("targetItemId", game.getCurrentTarget().getId());
            response.put("score", game.getScore());
            response.put("streak", game.getStreak());

            sendJsonResponse(resp, response);
        } catch (Exception e) {
            sendJsonError(resp, e.getMessage());
        }
    }
}
