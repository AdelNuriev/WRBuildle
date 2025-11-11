<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Бесконечный режим - Buildle.gg</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/challenge.css">
    <script src="${pageContext.request.contextPath}/js/infinite-game.js" defer></script>
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

    <main class="infinite-game">
        <h2>Бесконечный режим</h2>

        <div class="game-stats">
            <div class="stat">Счет: <span id="currentScore">${currentGame.score}</span></div>
            <div class="stat">Серия: <span id="currentStreak">${currentGame.streak}</span></div>
            <div class="stat">Подсказки: <span id="hintsUsed">${currentGame.hintsUsed}</span></div>
        </div>

        <c:if test="${not empty guessResult}">
            <div class="guess-result ${guessResult.correct ? 'correct' : 'incorrect'}">
                    ${guessResult.message}
                <c:if test="${guessResult.correct}">
                    +${guessResult.scoreEarned} очков!
                </c:if>
            </div>
        </c:if>

        <div class="game-content">
            <div class="attributes-section">
                <h3>Характеристики предмета:</h3>
                <div id="attributesList" class="attributes-list"></div>

                <c:if test="${not empty hint}">
                    <div class="hint-section">
                        <h4>Подсказка:</h4>
                        <p>${hint.attribute}: ${hint.value}</p>
                    </div>
                </c:if>

                <form action="${pageContext.request.contextPath}/infinite/hint" method="post">
                    <button type="submit" class="btn-secondary">Использовать подсказку</button>
                </form>
            </div>

            <div class="guess-section">
                <input type="text" id="searchInput" placeholder="Начните вводить название предмета...">
                <div id="itemsList" class="items-list"></div>

                <form action="${pageContext.request.contextPath}/infinite/guess" method="post" class="guess-form">
                    <input type="hidden" name="itemId" id="selectedItemId">
                    <button type="submit" class="btn-primary">Сделать предположение</button>
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
