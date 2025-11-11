package ru.itis.wr.servlets;

import ru.itis.wr.entities.User;
import ru.itis.wr.services.ShopService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/shop/*")
public class ShopServlet extends HttpServlet {
    private ShopService shopService;

    @Override
    public void init() throws ServletException {
        try {
            shopService = (ShopService) getServletContext().getAttribute("shopService");
            if (shopService == null) {
                throw new ServletException("ShopService not found in ServletContext");
            }
        } catch (Exception e) {
            throw new ServletException("Failed to initialize ShopServlet", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getAttribute("currentUser");

        try {
            var shopItems = shopService.getAvailableItems();
            var userInventory = shopService.getUserInventory(user.getId());

            req.setAttribute("shopItems", shopItems);
            req.setAttribute("userInventory", userInventory);
            req.setAttribute("userCoins", user.getCoins());
            req.getRequestDispatcher("/WEB-INF/views/shop.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Failed to load shop: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/shop.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        User user = (User) req.getAttribute("currentUser");

        try {
            if ("/purchase".equals(path)) {
                Long itemId = Long.parseLong(req.getParameter("itemId"));
                var result = shopService.purchaseItem(user.getId(), itemId);

                if (result.isSuccess()) {
                    req.setAttribute("success", "Item purchased successfully!");
                } else {
                    req.setAttribute("error", result.getMessage());
                }

                doGet(req, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            doGet(req, resp);
        }
    }
}
