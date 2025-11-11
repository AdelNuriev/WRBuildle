<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Магазин - WR-Buildle.gg</title>
    <link rel="stylesheet" href="/css/layout.css">
    <link rel="stylesheet" href="/css/shop.css">
    <script src="/js/shop.js" defer></script>
</head>
<body>
<div class="container">
    <header>
        <h1>WR-Buildle.gg</h1>
        <p class="subtitle">League of Legends Wild Rift Item Guessing Game</p>
        <nav class="main-nav">
            <a href="/dashboard">Главная</a>
            <a href="/daily">Ежедневный режим</a>
            <a href="/infinite">Бесконечный режим</a>
            <a href="/shop">Магазин</a>
            <a href="/profile">Профиль</a>
            <a href="/statistics">Статистика</a>
            <c:if test="${currentUser.role == 'ADMIN'}">
                <a href="/admin">Админ панель</a>
            </c:if>
            <a href="/auth/logout">Выйти</a>
        </nav>
    </header>

    <main class="shop-page">
        <h2>Магазин</h2>

        <div class="shop-header">
            <div class="user-coins">
                <h3>Ваши монеты: ${currentUser.coins} 🪙</h3>
            </div>
        </div>

        <c:if test="${not empty success}">
            <div class="success-message">${success}</div>
        </c:if>

        <c:if test="${not empty error}">
            <div class="error-message">${error}</div>
        </c:if>

        <div class="shop-categories">
            <button class="category-btn active" onclick="filterItems('all')">Все предметы</button>
            <button class="category-btn" onclick="filterItems('ICON')">Иконки</button>
            <button class="category-btn" onclick="filterItems('BACKGROUND')">Фоны</button>
            <button class="category-btn" onclick="filterItems('BORDER')">Рамки</button>
            <button class="category-btn" onclick="filterItems('FONT')">Шрифты</button>
        </div>

        <div class="shop-items">
            <c:forEach var="item" items="${shopItems}">
                <div class="shop-item" data-type="${item.type}">
                    <div class="item-image">
                        <img src="${item.imageUrl}" alt="${item.name}">
                        <div class="item-rarity ${item.rarity}">${item.rarity.displayName}</div>
                    </div>

                    <div class="item-info">
                        <h4>${item.name}</h4>
                        <p class="item-type">${item.type.displayName}</p>
                        <div class="item-price">
                                ${item.price} 🪙
                            <c:if test="${userInventory.stream().anyMatch(p -> p.shopItemId == item.id)}">
                                <span class="owned-badge">Куплено</span>
                            </c:if>
                        </div>
                    </div>

                    <div class="item-actions">
                        <c:choose>
                            <c:when test="${userInventory.stream().anyMatch(p -> p.shopItemId == item.id)}">
                                <button class="btn-secondary" disabled>Уже куплено</button>
                            </c:when>
                            <c:when test="${currentUser.coins >= item.price}">
                                <form action="/shop/purchase" method="post">
                                    <input type="hidden" name="itemId" value="${item.id}">
                                    <button type="submit" class="btn-primary">Купить</button>
                                </form>
                            </c:when>
                            <c:otherwise>
                                <button class="btn-secondary" disabled>Недостаточно монет</button>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </c:forEach>
        </div>
    </main>

    <footer>
        <p>&copy; 2025 WR-Buildle.gg - Не является собственностью Riot Games</p>
    </footer>
</div>
</body>
</html>
