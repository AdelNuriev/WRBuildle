package ru.itis.wr.handlers.infinite;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.itis.wr.entities.User;
import ru.itis.wr.handlers.BaseHandler;
import ru.itis.wr.handlers.challenges.ChallengeHandler;
import ru.itis.wr.services.ChallengeService;
import ru.itis.wr.services.ItemService;

import java.io.IOException;
import java.util.Random;

public class InfiniteGameHandler extends BaseHandler implements ChallengeHandler {
    private final ChallengeService challengeService;
    private final ItemService itemService;
    private final Random random;

    public InfiniteGameHandler(ChallengeService challengeService, ItemService itemService) {
        this.challengeService = challengeService;
        this.itemService = itemService;
        this.random = new Random();
    }

    @Override
    public boolean canHandle(String path, String method) {
        return "".equals(path) && "GET".equalsIgnoreCase(method);
    }

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        try {
            var currentGame = challengeService.getCurrentInfiniteGame(user.getId());

            var allItems = itemService.getAllItems();
            if (!allItems.isEmpty()) {
                var randomItem = allItems.get(random.nextInt(allItems.size()));
                req.setAttribute("targetItemId", randomItem.getId());
            } else {
                req.setAttribute("targetItemId", 1L);
            }

            req.setAttribute("currentGame", currentGame);
            req.setAttribute("gameCompleted", false);
            req.getRequestDispatcher("/WEB-INF/views/infinite/game.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Failed to load infinite mode: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/infinite/game.jsp").forward(req, resp);
        }
    }
}
