<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Главная - WR-Buildle.gg</title>
    <link rel="stylesheet" href="/css/layout.css">
    <link rel="stylesheet" href="/css/dashboard.css">
</head>
<body>
<div class="container">
    <header>
        <h1>WR-Buildle.gg</h1>
        <p class="subtitle">League of Legends Wild Rift Item Guessing Game</p>
        <nav class="main-nav">
            <a href="/dashboard">Главная</a>
            <a href="/daily">Ежедневный режим</a>
            <a href="/start/infinite">Бесконечный режим</a>
            <a href="/shop">Магазин</a>
            <a href="/profile">Профиль</a>
            <a href="/statistics">Статистика</a>
            <c:if test="${currentUser.role == 'ADMIN'}">
                <a href="/admin">Админ панель</a>
            </c:if>
            <a href="/auth/logout">Выйти</a>
        </nav>
    </header>

    <main class="dashboard">
        <div class="welcome-section">
            <h2>Добро пожаловать, ${currentUser.username}!</h2>
            <div class="user-stats">
                <div class="stat-card">
                    <h3>Уровень</h3>
                    <p class="stat-value">${currentUser.level}</p>
                </div>
                <div class="stat-card">
                    <h3>Монеты</h3>
                    <p class="stat-value">${currentUser.coins}</p>
                </div>
                <div class="stat-card">
                    <h3>Ежедневная серия</h3>
                    <p class="stat-value">${userStats.dailyStreak} дней</p>
                </div>
            </div>
        </div>

        <div class="daily-progress">
            <h3>Прогресс ежедневного вызова</h3>
            <div class="progress-bar">
                <c:forEach var="block" items="${todayResults}">
                    <div class="progress-block ${block.completed ? 'completed' : 'incomplete'}">
                        <span>${block.blockType.displayName}</span>
                    </div>
                </c:forEach>
            </div>
            <p>Завершено: ${completedBlocks}/6 блоков</p>
        </div>

        <div class="game-modes">
            <div class="game-mode" onclick="location.href='/daily'">
                <div class="mode-header">
                    <div class="mode-icon">📅</div>
                    <h3 class="mode-title">Ежедневный режим</h3>
                </div>
                <p class="mode-description">6 уникальных испытаний каждый день. Проверьте свои знания предметов Лиги Легенд!</p>
            </div>

            <div class="game-mode" onclick="location.href='/infinite'">
                <div class="mode-header">
                    <div class="mode-icon">∞</div>
                    <h3 class="mode-title">Бесконечный режим</h3>
                </div>
                <p class="mode-description">Угадывайте предметы по характеристикам без ограничений. Зарабатывайте очки и покажите свой результат!</p>
            </div>
        </div>
    </main>

    <footer>
        <p>&copy; 2025 WR-Buildle.gg - Не является собственностью Riot Games</p>
    </footer>
</div>
</body>
</html>
