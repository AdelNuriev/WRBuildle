package ru.itis.wr.servlets;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;

import com.fasterxml.jackson.databind.ObjectMapper;

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
                case "/edit-item":
                    showEditItem(req, resp);
                    break;
                case "/manage-recipe":
                    showManageRecipe(req, resp);
                    break;
                case "/challenges":
                    showChallengesManagement(req, resp);
                    break;
                case "/create-challenge":
                    showChallengeCreation(req, resp);
                    break;
                case "/statistics":
                    showAdminStatistics(req, resp);
                    break;
                case "/api/items":
                    serveItemsApi(req, resp);
                    break;
                case "/api/item-tree":
                    serveItemTreeApi(req, resp);
                    break;
                case "/api/full-item-tree":
                    serveFullItemTreeApi(req, resp);
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
                case "/add-component":
                    addComponent(req, resp);
                    break;
                case "/remove-component":
                    removeComponent(req, resp);
                    break;
                case "/create-daily-challenge":
                    createDailyChallenge(req, resp, admin);
                    break;
                case "/save-recipe-tree":
                    saveRecipeTree(req, resp);
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

    private void showEditItem(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Long itemId = Long.parseLong(req.getParameter("itemId"));
            var itemOpt = itemService.getItemById(itemId);

            if (itemOpt.isPresent()) {
                req.setAttribute("item", itemOpt.get());
                req.getRequestDispatcher("/WEB-INF/views/admin/edit-item.jsp").forward(req, resp);
            } else {
                req.setAttribute("error", "Item not found");
                showItemsManagement(req, resp);
            }
        } catch (Exception e) {
            req.setAttribute("error", "Error loading item: " + e.getMessage());
            showItemsManagement(req, resp);
        }
    }

    private void showManageRecipe(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Long itemId = Long.parseLong(req.getParameter("itemId"));
            var itemOpt = itemService.getItemById(itemId);

            if (itemOpt.isPresent()) {
                Item item = itemOpt.get();
                var components = itemService.getItemComponents(itemId);
                var itemTree = itemService.getFullItemTree(itemId);

                req.setAttribute("item", item);
                req.setAttribute("components", components);
                req.setAttribute("itemTree", itemTree);
                req.getRequestDispatcher("/WEB-INF/views/admin/manage-recipe.jsp").forward(req, resp);
            } else {
                req.setAttribute("error", "Item not found");
                showItemsManagement(req, resp);
            }
        } catch (NumberFormatException e) {
            req.setAttribute("error", "Invalid item ID");
            showItemsManagement(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Error loading recipe: " + e.getMessage());
            showItemsManagement(req, resp);
        }
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

    private void showAdminStatistics(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        var systemStats = adminService.getSystemStatistics();
        var leaderboard = adminService.getLeaderboard("all_time", LocalDate.now());

        req.setAttribute("systemStats", systemStats);
        req.setAttribute("leaderboard", leaderboard);
        req.getRequestDispatcher("/WEB-INF/views/admin/statistics.jsp").forward(req, resp);
    }

    private void serveItemsApi(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        var items = itemService.getAllItems();
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            json.append("{")
                    .append("\"id\":").append(item.getId())
                    .append(",\"name\":\"").append(item.getName()).append("\"")
                    .append(",\"iconUrl\":\"").append(item.getIconUrl()).append("\"")
                    .append(",\"cost\":").append(item.getCost())
                    .append("}");
            if (i < items.size() - 1) json.append(",");
        }
        json.append("]");

        resp.getWriter().write(json.toString());
    }

    private void serveFullItemTreeApi(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        try {
            Long itemId = Long.parseLong(req.getParameter("itemId"));
            ItemTree tree = itemService.getFullItemTree(itemId);

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");

            String json = convertFullTreeToJson(tree);
            resp.getWriter().write(json);

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private void createItem(HttpServletRequest req, HttpServletResponse resp, User admin)
            throws ServletException, IOException {
        try {
            String name = req.getParameter("name");
            String rarityStr = req.getParameter("rarity");
            String type = req.getParameter("type");
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
            createRequest.setActive(type.equals("ACTIVE") ? true : false);
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
            String type = req.getParameter("type");
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
            updateRequest.setActive(type.equals("ACTIVE") ? true : false);
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

    private void addComponent(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Long parentItemId = Long.parseLong(req.getParameter("parentItemId"));
            Long componentItemId = Long.parseLong(req.getParameter("componentItemId"));
            int quantity = Integer.parseInt(req.getParameter("quantity"));

            var parentItemOpt = itemService.getItemById(parentItemId);
            var componentItemOpt = itemService.getItemById(componentItemId);

            if (parentItemOpt.isEmpty() || componentItemOpt.isEmpty()) {
                req.setAttribute("error", "Item not found");
                showManageRecipe(req, resp);
                return;
            }

            ItemRecipe recipe = new ItemRecipe();
            recipe.setParentItemId(parentItemId);
            recipe.setComponentItemId(componentItemId);

            itemService.addRecipe(recipe);

            req.setAttribute("success", "Компонент успешно добавлен в сборку");
            req.setAttribute("itemId", parentItemId);
            showManageRecipe(req, resp);

        } catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("itemId", req.getParameter("parentItemId"));
            showManageRecipe(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Ошибка при добавлении компонента: " + e.getMessage());
            req.setAttribute("itemId", req.getParameter("parentItemId"));
            showManageRecipe(req, resp);
        }
    }

    private void removeComponent(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Long parentItemId = Long.parseLong(req.getParameter("parentItemId"));
            Long componentItemId = Long.parseLong(req.getParameter("componentItemId"));

            itemService.removeRecipe(parentItemId, componentItemId);

            req.setAttribute("success", "Компонент успешно удален");
            req.setAttribute("itemId", parentItemId);
            showManageRecipe(req, resp);

        } catch (Exception e) {
            req.setAttribute("error", "Error removing component: " + e.getMessage());
            showManageRecipe(req, resp);
        }
    }

    private void saveRecipeTree(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        System.out.println("=== SAVE RECIPE TREE DEBUG ===");

        try {
            StringBuilder requestBody = new StringBuilder();
            String line;
            try (BufferedReader reader = req.getReader()) {
                while ((line = reader.readLine()) != null) {
                    requestBody.append(line);
                }
            }

            String jsonString = requestBody.toString();
            System.out.println("Request body: " + jsonString);

            if (jsonString == null || jsonString.trim().isEmpty()) {
                System.out.println("ERROR: Request body is empty");
                sendError(resp, "Request body is empty");
                return;
            }

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            JsonNode rootNode = mapper.readTree(jsonString);

            JsonNode itemIdNode = rootNode.get("itemId");
            JsonNode recipeDataNode = rootNode.get("recipeData");

            if (itemIdNode == null || itemIdNode.isNull()) {
                System.out.println("ERROR: itemId is null in JSON");
                sendError(resp, "Item ID is required");
                return;
            }

            if (recipeDataNode == null || recipeDataNode.isNull()) {
                System.out.println("ERROR: recipeData is null in JSON");
                sendError(resp, "Recipe data is required");
                return;
            }

            Long itemId = itemIdNode.asLong();
            System.out.println("Parsed itemId: " + itemId);

            // Создаем дерево из recipeData
            ItemTree tree = parseTreeFromJson(recipeDataNode);

            if (tree == null) {
                System.out.println("ERROR: Failed to parse tree from recipeData");
                sendError(resp, "Failed to parse recipe tree");
                return;
            }

            System.out.println("Tree created, saving...");
            itemService.saveRecipeTree(itemId, tree);

            sendSuccess(resp, "Дерево сборки сохранено");

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            sendError(resp, "Ошибка: " + e.getMessage());
        }
    }

    private ItemTree parseTreeFromJson(JsonNode node) {
        if (node == null || !node.has("item")) {
            return null;
        }

        JsonNode itemNode = node.get("item");
        Item item = new Item();

        if (itemNode.has("id")) {
            item.setId(itemNode.get("id").asLong());
        }
        if (itemNode.has("name")) {
            item.setName(itemNode.get("name").asText());
        }
        if (itemNode.has("iconUrl")) {
            item.setIconUrl(itemNode.get("iconUrl").asText());
        }
        if (itemNode.has("cost")) {
            item.setCost((short) itemNode.get("cost").asInt());
        }

        ItemTree tree = new ItemTree(item);

        if (node.has("components")) {
            JsonNode componentsNode = node.get("components");
            if (componentsNode.isArray()) {
                for (JsonNode componentNode : componentsNode) {
                    ItemTree componentTree = parseTreeFromJson(componentNode);
                    if (componentTree != null) {
                        tree.addComponent(componentTree);
                    }
                }
            }
        }

        return tree;
    }

    private void sendSuccess(HttpServletResponse resp, String message) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write("{\"success\": true, \"message\": \"" + message + "\"}");
    }

    private void sendError(HttpServletResponse resp, String message) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write("{\"success\": false, \"message\": \"" + message + "\"}");
    }

    private void serveItemTreeApi(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        try {
            Long itemId = Long.parseLong(req.getParameter("itemId"));
            ItemTree tree = itemService.getFullItemTree(itemId);

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");

            String json = convertTreeToJson(tree);
            resp.getWriter().write(json);

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private String convertTreeToJson(ItemTree tree) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"item\": {");
        json.append("\"id\": ").append(tree.getItem().getId()).append(",");
        json.append("\"name\": \"").append(tree.getItem().getName()).append("\",");
        json.append("\"iconUrl\": \"").append(tree.getItem().getIconUrl()).append("\",");
        json.append("\"cost\": ").append(tree.getItem().getCost());
        json.append("},");
        json.append("\"components\": [");

        if (tree.getComponents() != null) {
            for (int i = 0; i < tree.getComponents().size(); i++) {
                ItemTree component = tree.getComponents().get(i);
                json.append("{");
                json.append("\"item\": {");
                json.append("\"id\": ").append(component.getItem().getId()).append(",");
                json.append("\"name\": \"").append(component.getItem().getName()).append("\",");
                json.append("\"iconUrl\": \"").append(component.getItem().getIconUrl()).append("\",");
                json.append("\"cost\": ").append(component.getItem().getCost());
                json.append("},");
                json.append("\"components\": []");
                json.append("}");
                if (i < tree.getComponents().size() - 1) {
                    json.append(",");
                }
            }
        }

        json.append("]");
        json.append("}");
        return json.toString();
    }

    private String convertFullTreeToJson(ItemTree tree) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"item\": {");
        json.append("\"id\": ").append(tree.getItem().getId()).append(",");
        json.append("\"name\": \"").append(tree.getItem().getName()).append("\",");
        json.append("\"iconUrl\": \"").append(tree.getItem().getIconUrl()).append("\",");
        json.append("\"cost\": ").append(tree.getItem().getCost());
        json.append("},");
        json.append("\"components\": ").append(convertComponentsToJson(tree.getComponents()));
        json.append("}");
        return json.toString();
    }

    private String convertComponentsToJson(java.util.List<ItemTree> components) {
        if (components == null || components.isEmpty()) {
            return "[]";
        }

        StringBuilder json = new StringBuilder();
        json.append("[");

        for (int i = 0; i < components.size(); i++) {
            ItemTree component = components.get(i);
            json.append("{");
            json.append("\"item\": {");
            json.append("\"id\": ").append(component.getItem().getId()).append(",");
            json.append("\"name\": \"").append(component.getItem().getName()).append("\",");
            json.append("\"iconUrl\": \"").append(component.getItem().getIconUrl()).append("\",");
            json.append("\"cost\": ").append(component.getItem().getCost());
            json.append("},");
            json.append("\"components\": ").append(convertComponentsToJson(component.getComponents()));
            json.append("}");
            if (i < components.size() - 1) {
                json.append(",");
            }
        }

        json.append("]");
        return json.toString();
    }
}
