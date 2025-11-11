// ChallengeHandlerFactory.java
package ru.itis.wr.handlers;

import ru.itis.wr.services.ChallengeService;

import java.util.ArrayList;
import java.util.List;

public class ChallengeHandlerFactory {
    private final List<ChallengeHandler> handlers;

    public ChallengeHandlerFactory(ChallengeService challengeService) {
        this.handlers = new ArrayList<>();
        initializeHandlers(challengeService);
    }

    private void initializeHandlers(ChallengeService challengeService) {
        handlers.add(new DailyOverviewHandler(challengeService));
        handlers.add(new IconChallengeHandler(challengeService));
        handlers.add(new ClassicChallengeHandler(challengeService));
        handlers.add(new AttributesChallengeHandler(challengeService));
        handlers.add(new MissingChallengeHandler(challengeService));
        handlers.add(new ImposterChallengeHandler(challengeService));
        handlers.add(new CostChallengeHandler(challengeService));

        handlers.add(new IconGuessHandler(challengeService));
        handlers.add(new ClassicGuessHandler(challengeService));
        handlers.add(new AttributesGuessHandler(challengeService));
        handlers.add(new MissingGuessHandler(challengeService));
        handlers.add(new ImposterGuessHandler(challengeService));
        handlers.add(new CostGuessHandler(challengeService));
    }

    public ChallengeHandler getHandler(String path, String method) {
        return handlers.stream()
                .filter(handler -> handler.canHandle(path, method))
                .findFirst()
                .orElse(null);
    }
}
