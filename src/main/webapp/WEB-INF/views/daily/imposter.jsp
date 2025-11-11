<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Найди лишний - WR-Buildle.gg</title>
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
        <h2>Найдите лишний предмет</h2>

        <c:if test="${userResult.completed}">
            <div class="completed-banner">
                ✅ Вы уже завершили это задание! Очков заработано: ${userResult.score}
            </div>
        </c:if>

        <div class="challenge-content">
            <div class="item-tree">
                <h3>Дерево сборки с лишним предметом:</h3>
                <div id="treeContainer" class="tree-container"></div>
            </div>

            <div class="guess-section">
                <p class="hint-text">В дереве сборки находится один лишний предмет, который не должен там быть. Найдите его!</p>

                <input type="text" id="searchInput" placeholder="Начните вводить название предмета...">
                <div id="itemsList" class="items-list"></div>

                <form action="/guess/imposter" method="post" class="guess-form">
                    <input type="hidden" name="itemId" id="selectedItemId">
                    <button type="submit" ${userResult.completed ? 'disabled' : ''} class="btn-primary">
                        ${userResult.completed ? 'Завершено' : 'Найти лишний предмет'}
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
