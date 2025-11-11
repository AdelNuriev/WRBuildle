<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Угадай по иконке - Buildle.gg</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/challenge.css">
    <script src="${pageContext.request.contextPath}/js/icon-challenge.js" defer></script>
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
        <h2>Угадайте предмет по иконке</h2>

        <c:if test="${userResult.completed}">
            <div class="completed-banner">
                ✅ Вы уже завершили это задание! Очков заработано: ${userResult.score}
            </div>
        </c:if>

        <div class="challenge-content">
            <div class="image-container">
                <img id="itemImage" src="${challenge.targetItem.iconUrl}"
                     class="challenge-image" alt="Загаданный предмет">
                <div class="image-controls">
                    <label for="difficulty">Сложность:</label>
                    <input type="range" id="difficulty" min="1" max="3" value="3">
                    <span id="difficultyLabel">Challenger</span>
                </div>
            </div>

            <div class="guess-section">
                <input type="text" id="searchInput" placeholder="Начните вводить название предмета...">
                <div id="itemsList" class="items-list"></div>

                <form action="${pageContext.request.contextPath}/guess/icon" method="post" class="guess-form">
                    <input type="hidden" name="itemId" id="selectedItemId">
                    <input type="hidden" name="difficulty" id="currentDifficulty" value="3">
                    <button type="submit" ${userResult.completed ? 'disabled' : ''} class="btn-primary">
                        ${userResult.completed ? 'Завершено' : 'Сделать предположение'}
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
