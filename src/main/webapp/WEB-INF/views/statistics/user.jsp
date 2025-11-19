<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Статистика - WR-Buildle.gg</title>
    <link rel="stylesheet" href="/css/layout.css">
    <link rel="stylesheet" href="/css/statistics.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <script src="/js/statistics.js" defer></script>
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

    <main class="statistics-page">
        <h2>Статистика игрока</h2>

        <div class="user-stats-overview">
            <div class="stat-card-large">
                <h3>Общая статистика</h3>
                <div class="stats-grid">
                    <div class="stat-item">
                        <span class="stat-label">Всего игр</span>
                        <span class="stat-value">${userStats.totalGames}</span>
                    </div>
                    <div class="stat-item">
                        <span class="stat-label">Побед</span>
                        <span class="stat-value">${userStats.gamesWon}</span>
                    </div>
                    <div class="stat-item">
                        <span class="stat-label">Процент побед</span>
                        <span class="stat-value">
                            <c:if test="${userStats.totalGames > 0}">
                                ${Math.round((userStats.gamesWon / userStats.totalGames * 100))}%
                            </c:if>
                            <c:if test="${userStats.totalGames == 0}">0%</c:if>
                        </span>
                    </div>
                    <div class="stat-item">
                        <span class="stat-label">Общий счет</span>
                        <span class="stat-value">${userStats.totalScore}</span>
                    </div>
                    <div class="stat-item">
                        <span class="stat-label">Ежедневная серия</span>
                        <span class="stat-value">${userStats.dailyStreak} дней</span>
                    </div>
                    <div class="stat-item">
                        <span class="stat-label">Лучший дневной счет</span>
                        <span class="stat-value">${userStats.bestDailyScore}</span>
                    </div>
                </div>
            </div>
        </div>

        <div class="charts-section">
            <h3>Прогресс по типам блоков</h3>

            <div class="chart-filters">
                <label>Период:</label>
                <select id="periodSelect">
                    <option value="7">7 дней</option>
                    <option value="14">14 дней</option>
                    <option value="30">30 дней</option>
                </select>

                <label>Тип блока:</label>
                <select id="blockTypeSelect">
                    <option value="all">Все блоки</option>
                    <c:forEach var="blockType" items="<%=ru.itis.wr.entities.BlockType.values()%>">
                        <option value="${blockType}">${blockType.displayName}</option>
                    </c:forEach>
                </select>
            </div>

            <div class="charts-container">
                <div class="chart-card">
                    <h4>Количество попыток по дням</h4>
                    <canvas id="attemptsChart" width="400" height="200"></canvas>
                </div>

                <div class="chart-card">
                    <h4>Заработанные очки по дням</h4>
                    <canvas id="scoreChart" width="400" height="200"></canvas>
                </div>
            </div>
        </div>

        <div class="block-stats-section">
            <h3>Статистика по типам блоков</h3>

            <div class="block-stats-grid">
                <c:forEach var="entry" items="${blockStats}">
                    <div class="block-stat-card">
                        <h4>${entry.key.displayName}</h4>
                        <div class="block-stat-details">
                            <div class="stat-detail">
                                <span>Всего игр:</span>
                                <span>${entry.value.totalGames}</span>
                            </div>
                            <div class="stat-detail">
                                <span>Побед:</span>
                                <span>${entry.value.wins}</span>
                            </div>
                            <div class="stat-detail">
                                <span>Процент побед:</span>
                                <span>${entry.value.winRate}%</span>
                            </div>
                            <div class="stat-detail">
                                <span>Среднее попыток:</span>
                                <span>${entry.value.avgAttempts}</span>
                            </div>
                            <div class="stat-detail">
                                <span>Всего очков:</span>
                                <span>${entry.value.totalScore}</span>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>
    </main>

    <footer>
        <p>&copy; 2025 WR-Buildle.gg - Не является собственностью Riot Games</p>
    </footer>
</div>
</body>
</html>
