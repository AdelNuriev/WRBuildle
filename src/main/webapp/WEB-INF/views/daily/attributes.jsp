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
    <link rel="stylesheet" href="/css/attributes-challenge.css">
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
        <div class="challenge-header">
            <h2>Угадайте предмет по атрибутам</h2>
            <div class="attempts-counter" id="attemptsCounter">0 попыток</div>
        </div>

        <c:if test="${userResult.completed}">
            <div class="completed-banner">
                ✅ Вы уже завершили это задание! Очков заработано: ${userResult.score}
            </div>
        </c:if>

        <div class="attributes-challenge-content">
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
                    <input type="text" id="searchInput" placeholder="Начните вводить название предмета..."
                           onkeyup="searchItems()" ${userResult.completed ? 'disabled' : ''}>
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
    window.challengeData = {
        completed: ${userResult.completed},
        targetItemId: ${targetItemId}
    };
</script>
</body>
</html>