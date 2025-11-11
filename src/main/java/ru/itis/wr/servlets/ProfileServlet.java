package ru.itis.wr.servlets;

import ru.itis.wr.entities.User;
import ru.itis.wr.services.ShopService;
import ru.itis.wr.services.StatisticsService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

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

            req.setAttribute("userStats", userStats);
            req.setAttribute("inventory", inventory);
            req.setAttribute("equippedItems", equippedItems);
            req.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(req, resp);
        } catch (Exception e) {
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
                Long itemId = Long.parseLong(req.getParameter("itemId"));
                shopService.equipItem(user.getId(), itemId);
                resp.sendRedirect(req.getContextPath() + "/profile");
            } else if ("/update-bio".equals(path)) {
                String bio = req.getParameter("bio");
                // Обновление био пользователя
                resp.sendRedirect(req.getContextPath() + "/profile");
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            doGet(req, resp);
        }
    }
}
