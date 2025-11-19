<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Бесконечный режим - WR-Buildle.gg</title>
    <link rel="stylesheet" href="/css/layout.css">
    <link rel="stylesheet" href="/css/challenge.css">
    <link rel="stylesheet" href="/css/attributes-challenge.css">
    <script src="/js/infinite-game.js" defer></script>
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

    <main class="infinite-game">
        <div class="game-header">
            <h2>Бесконечный режим - Угадай по атрибутам</h2>
            <div class="game-stats">
                <div class="stat">Счет: <span id="currentScore">${currentGame.score}</span></div>
                <div class="stat">Серия: <span id="currentStreak">${currentGame.streak}</span></div>
                <div class="stat">Попытки: <span id="attemptsCount">0</span></div>
                <div class="stat">Раунд: <span id="currentRound">1</span></div>
            </div>
        </div>

        <c:if test="${not empty error}">
            <div class="error-message">
                    ${error}
            </div>
        </c:if>

        <div class="attributes-challenge-content" id="gameContent">
            <div class="target-properties">
                <h3>Свойства загаданного предмета</h3>
                <div class="properties-grid">
                    <div class="property-card">
                        <div class="property-name">Редкость</div>
                        <div class="property-value unknown" id="rarityProperty">???</div>
                    </div>
                    <div class="property-card">
                        <div class="property-name">Тип эффекта</div>
                        <div class="property-value unknown" id="effectTypeProperty">???</div>
                    </div>
                    <div class="property-card">
                        <div class="property-name">Стоимость</div>
                        <div class="property-value unknown" id="costProperty">???</div>
                    </div>
                </div>
            </div>

            <div class="guess-interface">
                <div class="search-container">
                    <input type="text" id="searchInput" placeholder="Начните вводить название предмета...">
                    <div id="itemsList" class="items-grid"></div>
                </div>
            </div>

            <div class="guess-history-section">
                <h3>История попыток</h3>
                <div class="guess-history-table">
                    <div class="table-header">
                        <div class="header-cell icon-header">Иконка</div>
                        <div class="header-cell attributes-header">Свойства</div>
                        <div class="header-cell effect-type-header">Тип эффекта</div>
                        <div class="header-cell rarity-header">Редкость</div>
                        <div class="header-cell cost-header">Стоимость</div>
                    </div>
                    <div id="guessHistory" class="table-body"></div>
                </div>
            </div>
        </div>
    </main>

    <footer>
        <p>&copy; 2025 WR-Buildle.gg - Не является собственностью Riot Games</p>
    </footer>
</div>

<script>
    window.gameData = {
        currentScore: ${currentGame.score},
        currentStreak: ${currentGame.streak},
        targetItemId: ${targetItemId},
        currentRound: 1
    };
</script>
</body>
</html>
