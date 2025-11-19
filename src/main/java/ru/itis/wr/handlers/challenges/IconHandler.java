package ru.itis.wr.handlers.challenges;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.itis.wr.entities.BlockType;
import ru.itis.wr.entities.User;
import ru.itis.wr.handlers.BaseHandler;
import ru.itis.wr.services.ChallengeService;
import ru.itis.wr.services.ItemService;

import java.io.IOException;
import java.time.LocalDate;

public class IconHandler extends BaseHandler implements ChallengeHandler {
    private final ChallengeService challengeService;
    private final ItemService itemService;

    public IconHandler(ChallengeService challengeService, ItemService itemService) {
        this.challengeService = challengeService;
        this.itemService = itemService;
    }

    @Override
    public boolean canHandle(String path, String method) {
        return "/icon".equals(path) && "GET".equalsIgnoreCase(method);
    }

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        var challenge = challengeService.getChallengeBlock(LocalDate.now(), BlockType.ICON);
        var userResult = challengeService.getUserResult(user.getId(), LocalDate.now(), BlockType.ICON);
        var targetItem = itemService.getItemById(challenge.getTargetItemId())
                .orElseThrow(() -> new ServletException("Item not found for challenge"));

        req.setAttribute("challenge", challenge);
        req.setAttribute("targetItem", targetItem);
        req.setAttribute("userResult", userResult);
        req.getRequestDispatcher("/WEB-INF/views/daily/icon.jsp").forward(req, resp);
    }
}
