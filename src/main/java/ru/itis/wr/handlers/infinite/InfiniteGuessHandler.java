package ru.itis.wr.handlers.infinite;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.itis.wr.entities.User;
import ru.itis.wr.handlers.BaseHandler;
import ru.itis.wr.handlers.challenges.ChallengeHandler;
import ru.itis.wr.helper.GuessResult;
import ru.itis.wr.services.ChallengeService;
import ru.itis.wr.services.ItemService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class InfiniteGuessHandler extends BaseHandler implements ChallengeHandler {
    private final ChallengeService challengeService;
    private final ItemService itemService;
    private final Random random;

    public InfiniteGuessHandler(ChallengeService challengeService, ItemService itemService) {
        this.challengeService = challengeService;
        this.itemService = itemService;
        this.random = new Random();
    }

    @Override
    public boolean canHandle(String path, String method) {
        return "/guess/infinite".equals(path) && "POST".equalsIgnoreCase(method);
    }

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse resp, User user) throws IOException {
        try {
            Long itemId = Long.parseLong(req.getParameter("itemId"));
            Long targetItemId = Long.parseLong(req.getParameter("targetItemId"));

            var result = challengeService.processInfiniteGuess(user.getId(), itemId, targetItemId);
            user.earnCoins(result);
            if (result.isCorrect()) {
                var allItems = itemService.getAllItems();
                Long newTargetItemId = allItems.get(random.nextInt(allItems.size())).getId();

                Map<String, Object> additionalData = new HashMap<>();
                additionalData.put("newTargetItemId", newTargetItemId);
                additionalData.put("streak", result.getAdditionalData() != null ?
                        ((Map<?, ?>) result.getAdditionalData()).get("streak") : 1);

                result.setAdditionalData(additionalData);
            }

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
        response.put("totalScore", result.getScoreEarned());
        response.put("streak", result.getAdditionalData() != null ?
                ((Map<?, ?>) result.getAdditionalData()).get("streak") : 0);

        if (result.getAdditionalData() instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> additionalData = (Map<String, Object>) result.getAdditionalData();
            response.putAll(additionalData);
        }
        return response;
    }
}
