package ru.itis.wr.handlers.challenges;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.itis.wr.entities.*;
import ru.itis.wr.handlers.BaseHandler;
import ru.itis.wr.services.ChallengeService;

import java.io.IOException;
import java.time.LocalDate;

public class AttributesHandler extends BaseHandler implements ChallengeHandler {
    private final ChallengeService challengeService;

    public AttributesHandler(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    @Override
    public boolean canHandle(String path, String method) {
        return "/attributes".equals(path) && "GET".equalsIgnoreCase(method);
    }

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        var challenge = challengeService.getChallengeBlock(LocalDate.now(), BlockType.ATTRIBUTES);
        var userResult = challengeService.getUserResult(user.getId(), LocalDate.now(), BlockType.ATTRIBUTES);
        var targetItemId = challenge.getTargetItemId();

        req.setAttribute("challenge", challenge);
        req.setAttribute("userResult", userResult);
        req.setAttribute("targetItemId", targetItemId);
        req.getRequestDispatcher("/WEB-INF/views/daily/attributes.jsp").forward(req, resp);
    }
}
