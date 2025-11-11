<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Управление предметами - Buildle.gg</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
    <script src="${pageContext.request.contextPath}/js/admin-items.js" defer></script>
</head>
<body>
<div class="container">
    <header>
        <h1>Buildle.gg</h1>
        <p class="subtitle">League of Legends Wild Rift Item Guessing Game</p>
        <nav class="main-nav">
            <a href="${pageContext.request.contextPath}/dashboard">Главная</a>
            <a href="${pageContext.request.contextPath}/daily">Ежедневный режим</a>
            <a href="${pageContext.request.contextPath}/infinite">Бесконечный режим</a>
            <a href="${pageContext.request.contextPath}/shop">Магазин</a>
            <a href="${pageContext.request.contextPath}/profile">Профиль</a>
            <a href="${pageContext.request.contextPath}/statistics">Статистика</a>
            <c:if test="${currentUser.role == 'ADMIN'}">
                <a href="${pageContext.request.contextPath}/admin">Админ панель</a>
            </c:if>
            <a href="${pageContext.request.contextPath}/auth/logout">Выйти</a>
        </nav>
    </header>

    <main class="admin-page">
        <h2>Управление предметами</h2>

        <c:if test="${not empty success}">
            <div class="success-message">${success}</div>
        </c:if>

        <c:if test="${not empty error}">
            <div class="error-message">${error}</div>
        </c:if>

        <div class="admin-actions">
            <button onclick="showCreateForm()" class="btn-primary">Создать новый предмет</button>
        </div>

        <!-- Форма создания предмета -->
        <div id="createForm" class="create-form" style="display: none;">
            <h3>Создать новый предмет</h3>
            <form action="${pageContext.request.contextPath}/admin/create-item" method="post">
                <div class="form-row">
                    <div class="form-group">
                        <label>Название:</label>
                        <input type="text" name="name" required>
                    </div>
                    <div class="form-group">
                        <label>Редкость:</label>
                        <select name="rarity" required>
                            <option value="COMMON">Обычный</option>
                            <option value="BOOT">Сапоги</option>
                            <option value="EPIC">Эпический</option>
                            <option value="MYTHICAL">Мифический</option>
                            <option value="LEGENDARY">Легендарный</option>
                        </select>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label>Стоимость:</label>
                        <input type="number" name="cost" min="0" max="5000" required>
                    </div>
                    <div class="form-group">
                        <label>URL иконки:</label>
                        <input type="url" name="iconUrl" required>
                    </div>
                </div>

                <div class="form-group">
                    <label>Атрибуты:</label>
                    <div class="attributes-checkbox">
                        <c:forEach var="attr" items="<%=ru.itis.wr.entities.ItemAttributes.values()%>">
                            <label class="checkbox-label">
                                <input type="checkbox" name="attributes" value="${attr}">
                                    ${attr.displayName}
                            </label>
                        </c:forEach>
                    </div>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn-primary">Создать предмет</button>
                    <button type="button" onclick="hideCreateForm()" class="btn-secondary">Отмена</button>
                </div>
            </form>
        </div>

        <!-- Список предметов -->
        <div class="items-list-admin">
            <h3>Все предметы (${items.size()})</h3>

            <div class="items-grid-admin">
                <c:forEach var="item" items="${items}">
                    <div class="item-card-admin">
                        <div class="item-header">
                            <img src="${item.iconUrl}" alt="${item.name}" class="item-icon-admin">
                            <div class="item-info">
                                <h4>${item.name}</h4>
                                <p class="item-rarity ${item.rarity}">${item.rarity.displayName}</p>
                                <p class="item-cost">${item.cost} золота</p>
                            </div>
                        </div>

                        <div class="item-attributes">
                            <c:forEach var="attr" items="${item.attributes}">
                                <span class="attribute-tag">${attr.displayName}</span>
                            </c:forEach>
                        </div>

                        <div class="item-actions">
                            <button onclick="editItem(${item.id})" class="btn-small">Редактировать</button>
                            <form action="${pageContext.request.contextPath}/admin/update-item" method="post"
                                  style="display: inline;">
                                <input type="hidden" name="itemId" value="${item.id}">
                                <input type="hidden" name="isActive" value="false">
                                <button type="submit" class="btn-small btn-danger">Деактивировать</button>
                            </form>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>
    </main>

    <footer>
        <p>&copy; 2025 Buildle.gg - Not affiliated with Riot Games</p>
    </footer>
</div>
</body>
</html>
