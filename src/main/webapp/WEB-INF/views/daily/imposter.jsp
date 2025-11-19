<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Найди импостера - WR-Buildle.gg</title>
    <link rel="stylesheet" href="/css/layout.css">
    <link rel="stylesheet" href="/css/challenge.css">
    <script src="/js/imposter-challenge.js" defer></script>
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
        <h2>Найдите импостера в сборке</h2>

        <c:if test="${userResult.completed}">
            <div class="completed-banner">
                ✅ Вы уже завершили это задание! Очков заработано: ${userResult.score}
            </div>
        </c:if>

        <div class="challenge-content">
            <div class="tree-section">
                <h3>Сборка с импостером:</h3>
                <div id="treeContainer" class="tree-container">
                    <script id="treeData" type="application/json">
                        ${itemTreeJson}
                    </script>
                </div>
            </div>

            <div class="history-section">
                <h3>История попыток</h3>
                <div id="guessHistory" class="guess-history"></div>
            </div>

            <div class="search-section">
                <div class="search-container">
                    <input type="text" id="searchInput" placeholder="Начните вводить название предмета..."
                    ${userResult.completed ? 'disabled' : ''}>
                    <div id="itemsList" class="items-grid"></div>
                </div>

                <div class="guess-instruction">
                    Найдите и выберите лишний предмет (импостера) в сборке выше
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
        imposterItemId: ${extraItemId},
        targetItemId: ${challenge.targetItemId}
    };
</script>
</body>
</html>
