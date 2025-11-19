package ru.itis.wr.handlers.infinite;

import ru.itis.wr.handlers.challenges.ChallengeHandler;
import ru.itis.wr.services.ChallengeService;
import ru.itis.wr.services.ItemService;

import java.util.ArrayList;
import java.util.List;

public class InfiniteHandlerFactory {
    private final List<ChallengeHandler> handlers;

    public InfiniteHandlerFactory(ChallengeService challengeService, ItemService itemService) {
        this.handlers = new ArrayList<>();
        initializeHandlers(challengeService, itemService);
    }

    private void initializeHandlers(ChallengeService challengeService, ItemService itemService) {
        handlers.add(new InfiniteGameHandler(challengeService, itemService));
        handlers.add(new InfiniteGuessHandler(challengeService, itemService));
        handlers.add(new InfiniteStartHandler(challengeService));
    }

    public ChallengeHandler getHandler(String path, String method) {
        return handlers.stream()
                .filter(handler -> handler.canHandle(path, method))
                .findFirst()
                .orElse(null);
    }
}
