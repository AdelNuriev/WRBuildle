package ru.itis.wr.handlers.challenges;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.itis.wr.entities.User;
import ru.itis.wr.handlers.BaseHandler;
import ru.itis.wr.services.ItemService;

import java.io.IOException;

public class ItemsHandler extends BaseHandler implements ChallengeHandler {
    private final ItemService itemService;

    public ItemsHandler(ItemService itemService) {
        this.itemService = itemService;
    }

    @Override
    public boolean canHandle(String path, String method) {
        return "/api/items".equals(path) && "GET".equalsIgnoreCase(method);
    }

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse resp, User user) throws ServletException, IOException {
        try {
            var items = itemService.getAllItems();
            sendJsonResponse(resp, items);
        } catch (Exception e) {
            sendJsonError(resp, "Error loading items: " + e.getMessage());
        }
    }
}
