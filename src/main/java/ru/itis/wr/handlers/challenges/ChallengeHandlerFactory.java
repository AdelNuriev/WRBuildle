package ru.itis.wr.handlers.challenges;

import ru.itis.wr.services.ChallengeService;
import ru.itis.wr.services.ItemService;

import java.util.ArrayList;
import java.util.List;

public class ChallengeHandlerFactory {
    private final List<ChallengeHandler> handlers;

    public ChallengeHandlerFactory(ChallengeService challengeService, ItemService itemService) {
        this.handlers = new ArrayList<>();
        initializeHandlers(challengeService, itemService);
    }

    private void initializeHandlers(ChallengeService challengeService, ItemService itemService) {
        handlers.add(new DailyOverviewHandler(challengeService));
        handlers.add(new IconHandler(challengeService, itemService));
        handlers.add(new ClassicHandler(challengeService));
        handlers.add(new AttributesHandler(challengeService));
        handlers.add(new MissingHandler(challengeService));
        handlers.add(new ImposterHandler(challengeService));
        handlers.add(new CostHandler(challengeService, itemService));

        handlers.add(new IconGuessHandler(challengeService));
        handlers.add(new ClassicGuessHandler(challengeService));
        handlers.add(new AttributesGuessHandler(challengeService));
        handlers.add(new MissingGuessHandler(challengeService));
        handlers.add(new ImposterGuessHandler(challengeService));
        handlers.add(new CostGuessHandler(challengeService));

        handlers.add(new ItemsHandler(itemService));
    }

    public ChallengeHandler getHandler(String path, String method) {
        return handlers.stream()
                .filter(handler -> handler.canHandle(path, method))
                .findFirst()
                .orElse(null);
    }
}
