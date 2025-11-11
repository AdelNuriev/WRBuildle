<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Угадай по атрибутам - WR-Buildle.gg</title>
    <link rel="stylesheet" href="/css/layout.css">
    <link rel="stylesheet" href="/css/challenge.css">
    <script src="/js/attributes-challenge.js" defer></script>
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

    <main class="challenge-page">
        <h2>Угадайте предмет по характеристикам</h2>

        <c:if test="${userResult.completed}">
            <div class="completed-banner">
                ✅ Вы уже завершили это задание! Очков заработано: ${userResult.score}
            </div>
        </c:if>

        <div class="challenge-content">
            <div class="attributes-section">
                <h3>Характеристики загаданного предмета:</h3>

                <div class="attributes-grid">
                    <div class="attribute-card">
                        <h4>Стоимость</h4>
                        <div class="attribute-value">${challenge.targetItem.cost} золота</div>
                    </div>

                    <div class="attribute-card">
                        <h4>Редкость</h4>
                        <div class="attribute-value ${challenge.targetItem.rarity}">
                            ${challenge.targetItem.rarity.displayName}
                        </div>
                    </div>

                    <div class="attribute-card">
                        <h4>Основные характеристики</h4>
                        <div class="attribute-value">
                            <c:forEach var="attr" items="${challenge.targetItem.attributes}" varStatus="status">
                                ${attr.displayName}<c:if test="${!status.last}">, </c:if>
                            </c:forEach>
                        </div>
                    </div>

                    <div class="attribute-card">
                        <h4>Тип предмета</h4>
                        <div class="attribute-value">
                            <c:choose>
                                <c:when test="${challenge.targetItem.cost >= 3000}">Легендарный</c:when>
                                <c:when test="${challenge.targetItem.cost >= 2000}">Мифический</c:when>
                                <c:when test="${challenge.targetItem.cost >= 1000}">Эпический</c:when>
                                <c:when test="${challenge.targetItem.name.contains('Boots')}">Сапоги</c:when>
                                <c:otherwise>Обычный</c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>

                <div class="attempts-info">
                    <p>Попыток использовано: ${userResult.attempts}</p>
                    <c:if test="${userResult.attempts > 0 && !userResult.completed}">
                        <p class="hint-text">Подсказка: предмет связан с ${challenge.targetItem.attributes[0].displayName}</p>
                    </c:if>
                </div>
            </div>

            <div class="guess-section">
                <input type="text" id="searchInput" placeholder="Начните вводить название предмета...">
                <div id="itemsList" class="items-list"></div>

                <form action="/guess/attributes" method="post" class="guess-form">
                    <input type="hidden" name="itemId" id="selectedItemId">
                    <button type="submit" ${userResult.completed ? 'disabled' : ''} class="btn-primary">
                        ${userResult.completed ? 'Завершено' : 'Сделать предположение'}
                    </button>
                </form>
            </div>
        </div>
    </main>

    <footer>
        <p>&copy; 2025 WR-Buildle.gg - Не является собственностью Riot Games</p>
    </footer>
</div>
</body>
</html>
