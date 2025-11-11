package ru.itis.wr.handlers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.itis.wr.entities.BlockType;
import ru.itis.wr.entities.User;
import ru.itis.wr.services.ChallengeService;

import java.io.IOException;
import java.time.LocalDate;

public class IconChallengeHandler extends BaseChallengeHandler implements ChallengeHandler {
    private final ChallengeService challengeService;

    public IconChallengeHandler(ChallengeService challengeService) {
        this.challengeService = challengeService;
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

        req.setAttribute("challenge", challenge);
        req.setAttribute("userResult", userResult);
        req.getRequestDispatcher("/WEB-INF/views/daily/icon.jsp").forward(req, resp);
    }
}
