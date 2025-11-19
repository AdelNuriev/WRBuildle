package ru.itis.wr.servlets;

import ru.itis.wr.entities.User;
import ru.itis.wr.entities.UserPurchase;
import ru.itis.wr.entities.ShopItem;
import ru.itis.wr.services.ShopService;
import ru.itis.wr.services.StatisticsService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/profile/*")
public class ProfileServlet extends HttpServlet {
    private ShopService shopService;
    private StatisticsService statisticsService;

    @Override
    public void init() throws ServletException {
        try {
            shopService = (ShopService) getServletContext().getAttribute("shopService");
            statisticsService = (StatisticsService) getServletContext().getAttribute("statisticsService");

            if (shopService == null) {
                throw new ServletException("ShopService not found in ServletContext");
            }
            if (statisticsService == null) {
                throw new ServletException("StatisticsService not found in ServletContext");
            }
        } catch (Exception e) {
            throw new ServletException("Failed to initialize ProfileServlet", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getAttribute("currentUser");

        try {
            var userStats = statisticsService.getUserStatistics(user.getId());
            var inventory = shopService.getUserInventory(user.getId());
            var equippedItems = shopService.getEquippedItems(user.getId());

            Set<Long> equippedPurchaseIds = equippedItems.stream()
                    .map(UserPurchase::getId)
                    .collect(Collectors.toSet());

            List<Map<String, Object>> inventoryWithItems = new ArrayList<>();
            List<ShopItem> availableItems = shopService.getAvailableItems();

            for (UserPurchase purchase : inventory) {
                var itemOpt = availableItems.stream()
                        .filter(shopItem -> shopItem.getId().equals(purchase.getShopItemId()))
                        .findFirst();

                if (itemOpt.isPresent()) {
                    ShopItem shopItem = itemOpt.get();
                    boolean isEquipped = equippedPurchaseIds.contains(purchase.getId());

                    Map<String, Object> inventoryItem = new HashMap<>();
                    inventoryItem.put("purchaseId", purchase.getId());
                    inventoryItem.put("shopItem", shopItem);
                    inventoryItem.put("equipped", isEquipped);
                    inventoryItem.put("purchasedAt", purchase.getPurchasedAt());

                    inventoryWithItems.add(inventoryItem);
                }
            }

            req.setAttribute("userStats", userStats);
            req.setAttribute("inventory", inventoryWithItems);
            req.setAttribute("equippedItems", equippedItems);
            req.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Failed to load profile: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        User user = (User) req.getAttribute("currentUser");

        try {
            if ("/equip".equals(path)) {
                Long purchaseId = Long.parseLong(req.getParameter("itemId"));
                boolean success = shopService.equipItem(user.getId(), purchaseId);
                if (success) {
                    req.setAttribute("success", "Предмет успешно экипирован!");
                } else {
                    req.setAttribute("error", "Не удалось экипировать предмет");
                }
                doGet(req, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", e.getMessage());
            doGet(req, resp);
        }
    }
}