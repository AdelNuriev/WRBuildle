<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Угадай стоимость - Buildle.gg</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/challenge.css">
    <script src="${pageContext.request.contextPath}/js/cost-challenge.js" defer></script>
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

    <main class="challenge-page">
        <h2>Угадайте стоимость предмета</h2>

        <c:if test="${userResult.completed}">
            <div class="completed-banner">
                ✅ Вы уже завершили это задание! Очков заработано: ${userResult.score}
            </div>
        </c:if>

        <div class="challenge-content">
            <div class="item-display">
                <h3>Загаданный предмет:</h3>
                <div class="item-card">
                    <img src="${challenge.targetItem.iconUrl}" alt="${challenge.targetItem.name}" class="item-icon-large">
                    <h4>${challenge.targetItem.name}</h4>
                    <p class="item-rarity ${challenge.targetItem.rarity}">
                        ${challenge.targetItem.rarity.displayName}
                    </p>
                    <div class="item-attributes">
                        <c:forEach var="attr" items="${challenge.targetItem.attributes}" varStatus="status">
                            <span class="attribute-tag">${attr.displayName}</span>
                        </c:forEach>
                    </div>
                </div>
            </div>

            <div class="guess-section">
                <h3>Какова стоимость этого предмета?</h3>

                <div class="cost-input-section">
                    <input type="number" id="costInput" name="guessedCost"
                           min="0" max="5000" step="50" placeholder="Введите стоимость..."
                           class="cost-input">
                    <span class="gold-symbol">золота</span>
                </div>

                <div class="cost-hints">
                    <p>Подсказки:</p>
                    <ul>
                        <li>Обычные предметы: 0-999 золота</li>
                        <li>Эпические предметы: 1000-1999 золота</li>
                        <li>Мифические предметы: 2000-2999 золота</li>
                        <li>Легендарные предметы: 3000+ золота</li>
                        <li>Сапоги: обычно 300-1100 золота</li>
                    </ul>
                </div>

                <c:if test="${not empty guessResult}">
                    <div class="guess-feedback ${guessResult.correct ? 'correct' : 'incorrect'}">
                            ${guessResult.message}
                        <c:if test="${!guessResult.correct}">
                            <p>Попробуйте еще раз!</p>
                        </c:if>
                    </div>
                </c:if>

                <form action="${pageContext.request.contextPath}/guess/cost" method="post" class="guess-form">
                    <input type="hidden" name="guessedCost" id="guessedCostValue">
                    <button type="button" onclick="submitGuess()" ${userResult.completed ? 'disabled' : ''}
                            class="btn-primary">
                        ${userResult.completed ? 'Завершено' : 'Проверить стоимость'}
                    </button>
                </form>
            </div>
        </div>
    </main>

    <footer>
        <p>&copy; 2025 Buildle.gg - Not affiliated with Riot Games</p>
    </footer>
</div>
</body>
</html>
