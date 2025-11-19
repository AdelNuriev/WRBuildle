package ru.itis.wr.handlers.challenges;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.itis.wr.entities.*;
import ru.itis.wr.handlers.BaseHandler;
import ru.itis.wr.services.ChallengeService;

import java.io.IOException;
import java.time.LocalDate;

public class MissingHandler extends BaseHandler implements ChallengeHandler {
    private final ChallengeService challengeService;
    private final ObjectMapper objectMapper;

    public MissingHandler(ChallengeService challengeService) {
        this.challengeService = challengeService;
        objectMapper = new ObjectMapper();
    }

    @Override
    public boolean canHandle(String path, String method) {
        return "/missing".equals(path) && "GET".equalsIgnoreCase(method);
    }

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        var challenge = challengeService.getChallengeBlock(LocalDate.now(), BlockType.MISSING);
        var userResult = challengeService.getUserResult(user.getId(), LocalDate.now(), BlockType.MISSING);
        var itemTree = challengeService.getItemTree(challenge.getTargetItemId());
        Long extraItemId = challenge.getExtraItemId();

        String itemTreeJson = "{}";
        try {
            itemTreeJson = objectMapper.writeValueAsString(itemTree);
        } catch (Exception e) {
            itemTreeJson = "{\"error\":\"Failed to load tree data\"}";
        }

        req.setAttribute("challenge", challenge);
        req.setAttribute("userResult", userResult);
        req.setAttribute("itemTree", itemTree);
        req.setAttribute("itemTreeJson", itemTreeJson);
        req.setAttribute("extraItemId", extraItemId);
        req.getRequestDispatcher("/WEB-INF/views/daily/missing.jsp").forward(req, resp);
    }
}
