<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Угадай по иконке - WR-Buildle.gg</title>
    <link rel="stylesheet" href="/css/layout.css">
    <link rel="stylesheet" href="/css/challenge.css">
    <script src="/js/icon-challenge.js" defer></script>
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
        <h2>Угадайте предмет по иконке</h2>

        <c:if test="${userResult.completed}">
            <div class="completed-banner">
                ✅ Вы уже завершили это задание! Очков заработано: ${userResult.score}
            </div>
        </c:if>

        <div class="challenge-content">
            <div class="image-section">
                <div class="image-container">
                    <img id="itemImage" src="${targetItem.iconUrl}"
                         class="challenge-image" alt="Загаданный предмет">
                </div>

                <div class="difficulty-controls">
                    <label for="difficulty">Сложность:</label>
                    <input type="range" id="difficulty" min="1" max="3" value="3">
                    <span id="difficultyLabel">Challenger</span>
                    <div class="difficulty-info">
                        <small>Легко (25 очков) | Средне (50 очков) | Сложно (100 очков)</small>
                    </div>
                </div>
            </div>

            <div class="history-section">
                <h3>История попыток</h3>
                <div id="guessHistory" class="guess-history"></div>
            </div>

            <div class="search-section">
                <div class="search-container">
                    <input type="text" id="searchInput" placeholder="Начните вводить название предмета..."
                           oninput="searchItems()" ${userResult.completed ? 'disabled' : ''}>
                    <div id="itemsList" class="items-grid"></div>
                </div>
            </div>
        </div>
    </main>

    <footer>
        <p>&copy; 2025 WR-Buildle.gg - Не является собственностью Riot Games</p>
    </footer>
</div>

<script>
    window.challengeData = {
        completed: ${userResult.completed},
        targetItemIcon: "${targetItem.iconUrl}"
    };
</script>
</body>
</html>
