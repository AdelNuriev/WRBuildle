<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Классический режим - WR-Buildle.gg</title>
    <link rel="stylesheet" href="/css/layout.css">
    <link rel="stylesheet" href="/css/challenge.css">
    <script src="/js/classic-challenge.js" defer></script>
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
        <h2>Соберите дерево предметов</h2>

        <c:if test="${userResult.completed}">
            <div class="completed-banner">
                ✅ Вы уже завершили это задание! Очков заработано: ${userResult.score}
            </div>
        </c:if>

        <div class="challenge-content">
            <div class="item-tree">
                <h3>Дерево сборки:</h3>
                <div id="treeContainer" class="tree-container"></div>
            </div>

            <div class="guess-section">
                <input type="text" id="searchInput" placeholder="Начните вводить название предмета...">
                <div id="itemsList" class="items-list"></div>

                <form action="/guess/classic" method="post" class="guess-form">
                    <input type="hidden" name="itemId" id="selectedItemId">
                    <input type="hidden" name="guessType" id="guessType" value="component">
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
