package ru.itis.wr.handlers.challenges;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.itis.wr.entities.BlockType;
import ru.itis.wr.entities.User;
import ru.itis.wr.handlers.BaseHandler;
import ru.itis.wr.services.ChallengeService;

import java.io.IOException;
import java.time.LocalDate;

public class ClassicHandler extends BaseHandler implements ChallengeHandler {
    private final ChallengeService challengeService;
    private final ObjectMapper objectMapper;

    public ClassicHandler(ChallengeService challengeService) {
        this.challengeService = challengeService;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public boolean canHandle(String path, String method) {
        return "/classic".equals(path) && "GET".equalsIgnoreCase(method);
    }

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        try {
            var challenge = challengeService.getChallengeBlock(LocalDate.now(), BlockType.CLASSIC);
            var userResult = challengeService.getUserResult(user.getId(), LocalDate.now(), BlockType.CLASSIC);
            var itemTree = challengeService.getItemTree(challenge.getTargetItemId());

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

        } catch (Exception e) {
            req.setAttribute("error", "Ошибка загрузки классического режима: " + e.getMessage());
            req.setAttribute("itemTreeJson", "{\"error\":\"Failed to load challenge\"}");
        }

        req.getRequestDispatcher("/WEB-INF/views/daily/classic.jsp").forward(req, resp);
    }
}
