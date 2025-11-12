package ru.itis.wr.servlets;

import ru.itis.wr.dto.ChallengeBlockCreateRequest;
import ru.itis.wr.dto.ItemCreateRequest;
import ru.itis.wr.dto.ItemUpdateRequest;
import ru.itis.wr.entities.*;
import ru.itis.wr.services.AdminService;
import ru.itis.wr.services.ItemService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/admin/*")
public class AdminServlet extends HttpServlet {
    private AdminService adminService;
    private ItemService itemService;

    @Override
    public void init() throws ServletException {
        try {
            adminService = (AdminService) getServletContext().getAttribute("adminService");
            itemService = (ItemService) getServletContext().getAttribute("itemService");

            if (adminService == null) {
                throw new ServletException("AdminService not found in ServletContext");
            }
            if (itemService == null) {
                throw new ServletException("ItemService not found in ServletContext");
            }
        } catch (Exception e) {
            throw new ServletException("Failed to initialize AdminServlet", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getAttribute("currentUser");

        String path = req.getPathInfo();
        if (path == null) path = "";

        try {
            switch (path) {
                case "":
                case "/":
                    showAdminDashboard(req, resp);
                    break;
                case "/items":
                    showItemsManagement(req, resp);
                    break;
                case "/challenges":
                    showChallengesManagement(req, resp);
                    break;
                case "/create-challenge":
                    showChallengeCreation(req, resp);
                    break;
                case "/users":
                    showUsersManagement(req, resp);
                    break;
                case "/statistics":
                    showAdminStatistics(req, resp);
                    break;
                default:
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            req.setAttribute("error", "Admin error: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/admin/error.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path == null) path = "";

        User admin = (User) req.getAttribute("currentUser");

        try {
            switch (path) {
                case "/create-item":
                    createItem(req, resp, admin);
                    break;
                case "/update-item":
                    updateItem(req, resp);
                    break;
                case "/create-daily-challenge":
                    createDailyChallenge(req, resp, admin);
                    break;
                case "/update-user":
                    updateUser(req, resp);
                    break;
                default:
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            doGet(req, resp);
        }
    }

    private void showAdminDashboard(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        var systemStats = adminService.getSystemStatistics();
        var recentChallenges = adminService.getRecentChallenges(7);

        req.setAttribute("systemStats", systemStats);
        req.setAttribute("recentChallenges", recentChallenges);
        req.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(req, resp);
    }

    private void showItemsManagement(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        var allItems = itemService.getAllItems();
        req.setAttribute("items", allItems);
        req.getRequestDispatcher("/WEB-INF/views/admin/items.jsp").forward(req, resp);
    }

    private void showChallengesManagement(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        var challenges = adminService.getChallengesForDate(LocalDate.now());
        req.setAttribute("challenges", challenges);
        req.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(req, resp);
    }

    private void showChallengeCreation(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        var allItems = itemService.getAllItems();
        req.setAttribute("items", allItems);
        req.getRequestDispatcher("/WEB-INF/views/admin/create-challenge.jsp").forward(req, resp);
    }

    private void showUsersManagement(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int page = Integer.parseInt(req.getParameter("page") != null ? req.getParameter("page") : "1");
        int size = Integer.parseInt(req.getParameter("size") != null ? req.getParameter("size") : "20");

        var users = adminService.getAllUsers(page, size);
        req.setAttribute("users", users);
        req.setAttribute("currentPage", page);
        req.setAttribute("pageSize", size);
        req.getRequestDispatcher("/WEB-INF/views/admin/users.jsp").forward(req, resp);
    }

    private void showAdminStatistics(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        var systemStats = adminService.getSystemStatistics();
        var leaderboard = adminService.getLeaderboard("all_time", LocalDate.now());

        req.setAttribute("systemStats", systemStats);
        req.setAttribute("leaderboard", leaderboard);
        req.getRequestDispatcher("/WEB-INF/views/admin/statistics.jsp").forward(req, resp);
    }

    private void createItem(HttpServletRequest req, HttpServletResponse resp, User admin)
            throws ServletException, IOException {
        try {
            String name = req.getParameter("name");
            String rarityStr = req.getParameter("rarity");
            String costStr = req.getParameter("cost");
            String iconUrl = req.getParameter("iconUrl");
            String[] attributes = req.getParameterValues("attributes");

            if (name == null || rarityStr == null || costStr == null) {
                req.setAttribute("error", "Name, rarity and cost are required");
                showItemsManagement(req, resp);
                return;
            }

            var createRequest = new ItemCreateRequest();
            createRequest.setName(name);
            createRequest.setRarity(ItemRarity.valueOf(rarityStr));
            createRequest.setCost(Short.parseShort(costStr));
            createRequest.setIconUrl(iconUrl);

            if (attributes != null) {
                ItemAttributes[] itemAttributes = new ItemAttributes[attributes.length];
                for (int i = 0; i < attributes.length; i++) {
                    itemAttributes[i] = ItemAttributes.valueOf(attributes[i]);
                }
                createRequest.setAttributes(itemAttributes);
            }

            Item createdItem = adminService.createItem(createRequest);
            req.setAttribute("success", "Item '" + createdItem.getName() + "' created successfully");
            showItemsManagement(req, resp);

        } catch (Exception e) {
            req.setAttribute("error", "Failed to create item: " + e.getMessage());
            showItemsManagement(req, resp);
        }
    }

    private void updateItem(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Long itemId = Long.parseLong(req.getParameter("itemId"));
            String name = req.getParameter("name");
            String rarityStr = req.getParameter("rarity");
            String costStr = req.getParameter("cost");
            String iconUrl = req.getParameter("iconUrl");

            var updateRequest = new ItemUpdateRequest();
            updateRequest.setName(name);

            if (rarityStr != null) {
                updateRequest.setRarity(ItemRarity.valueOf(rarityStr));
            }
            if (costStr != null) {
                updateRequest.setCost((short) Integer.parseInt(costStr));
            }
            updateRequest.setIconUrl(iconUrl);

            Item updatedItem = adminService.updateItem(itemId, updateRequest);
            req.setAttribute("success", "Item '" + updatedItem.getName() + "' updated successfully");
            showItemsManagement(req, resp);

        } catch (Exception e) {
            req.setAttribute("error", "Failed to update item: " + e.getMessage());
            showItemsManagement(req, resp);
        }
    }

    private void createDailyChallenge(HttpServletRequest req, HttpServletResponse resp, User admin)
            throws ServletException, IOException {
        try {
            String dateStr = req.getParameter("challengeDate");
            LocalDate challengeDate = dateStr != null ? LocalDate.parse(dateStr) : LocalDate.now();

            DailyChallenge challenge = adminService.createDailyChallenge(challengeDate, admin.getId());

            for (BlockType blockType : BlockType.values()) {
                String targetItemIdParam = blockType.name().toLowerCase() + "TargetItemId";
                String extraItemIdParam = blockType.name().toLowerCase() + "ExtraItemId";

                String targetItemIdStr = req.getParameter(targetItemIdParam);
                String extraItemIdStr = req.getParameter(extraItemIdParam);

                if (targetItemIdStr != null) {
                    var blockRequest = new ChallengeBlockCreateRequest();
                    blockRequest.setBlockType(blockType);
                    blockRequest.setTargetItemId(Long.parseLong(targetItemIdStr));

                    if (extraItemIdStr != null) {
                        blockRequest.setExtraItemId(Long.parseLong(extraItemIdStr));
                    }

                    String settings = "{}";
                    if (blockType == BlockType.ICON) {
                        settings = "{\"difficulty\": \"medium\"}";
                    }
                    blockRequest.setSettings(settings);

                    adminService.createChallengeBlock(challenge.getId(), blockRequest);
                }
            }

            req.setAttribute("success", "Daily challenge for " + challengeDate + " created successfully");
            showChallengesManagement(req, resp);

        } catch (Exception e) {
            req.setAttribute("error", "Failed to create daily challenge: " + e.getMessage());
            showChallengeCreation(req, resp);
        }
    }

    private void updateUser(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Long userId = Long.parseLong(req.getParameter("userId"));
            String roleStr = req.getParameter("role");
            String coinsStr = req.getParameter("coins");

            if (roleStr != null) {
                Role newRole = Role.valueOf(roleStr);
                adminService.updateUserRole(userId, newRole);
                req.setAttribute("success", "User role updated successfully");
            }

            if (coinsStr != null) {
                int coins = Integer.parseInt(coinsStr);
                adminService.grantCoins(userId, coins);
                req.setAttribute("success", coins + " coins granted to user");
            }

            showUsersManagement(req, resp);

        } catch (Exception e) {
            req.setAttribute("error", "Failed to update user: " + e.getMessage());
            showUsersManagement(req, resp);
        }
    }
}
