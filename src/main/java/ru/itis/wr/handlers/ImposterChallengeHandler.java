package ru.itis.wr.handlers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.itis.wr.entities.*;
import ru.itis.wr.services.ChallengeService;

import java.io.IOException;
import java.time.LocalDate;

public class ImposterChallengeHandler extends BaseChallengeHandler implements ChallengeHandler {
    private final ChallengeService challengeService;

    public ImposterChallengeHandler(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    @Override
    public boolean canHandle(String path, String method) {
        return "/imposter".equals(path) && "GET".equalsIgnoreCase(method);
    }

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        ChallengeBlock challenge = challengeService.getChallengeBlock(LocalDate.now(), BlockType.CLASSIC);
        UserResult userResult = challengeService.getUserResult(user.getId(), LocalDate.now(), BlockType.CLASSIC);
        ItemTree itemTree = challengeService.getItemTree(challenge.getTargetItemId());

        req.setAttribute("challenge", challenge);
        req.setAttribute("userResult", userResult);
        req.setAttribute("itemTree", itemTree);
        req.getRequestDispatcher("/WEB-INF/views/daily/imposter.jsp").forward(req, resp);
    }
}
