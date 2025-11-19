package ru.itis.wr.handlers.challenges;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.itis.wr.entities.*;
import ru.itis.wr.handlers.BaseHandler;
import ru.itis.wr.services.ChallengeService;
import ru.itis.wr.services.ItemService;

import java.io.IOException;
import java.time.LocalDate;

public class CostHandler extends BaseHandler implements ChallengeHandler {
    private final ChallengeService challengeService;
    private final ItemService itemService;
    private final ObjectMapper objectMapper;

    public CostHandler(ChallengeService challengeService, ItemService itemService) {
        this.challengeService = challengeService;
        this.itemService = itemService;
        objectMapper = new ObjectMapper();
    }

    @Override
    public boolean canHandle(String path, String method) {
        return "/cost".equals(path) && "GET".equalsIgnoreCase(method);
    }

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        var challenge = challengeService.getChallengeBlock(LocalDate.now(), BlockType.COST);
        var userResult = challengeService.getUserResult(user.getId(), LocalDate.now(), BlockType.COST);
        var targetItem = itemService.getItemById(challenge.getTargetItemId())
                .orElseThrow(() -> new ServletException("Item not found for challenge"));
        var itemTree = itemService.getFullItemTree(challenge.getTargetItemId());

        String itemTreeJson = "{}";
        try {
            itemTreeJson = objectMapper.writeValueAsString(itemTree);
        } catch (Exception e) {
            itemTreeJson = "{\"error\":\"Failed to load tree data\"}";
        }

        req.setAttribute("challenge", challenge);
        req.setAttribute("userResult", userResult);
        req.setAttribute("targetItem", targetItem);
        req.setAttribute("itemTree", itemTree);
        req.setAttribute("itemTreeJson", itemTreeJson);
        req.getRequestDispatcher("/WEB-INF/views/daily/cost.jsp").forward(req, resp);
    }
}
