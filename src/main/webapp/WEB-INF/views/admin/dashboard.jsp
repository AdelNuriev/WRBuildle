<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Админ панель - Buildle.gg</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
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

    <main class="admin-dashboard">
        <h2>Админ панель</h2>

        <div class="admin-stats-grid">
            <div class="admin-stat-card">
                <h3>Всего пользователей</h3>
                <p class="stat-number">${systemStats.totalUsers}</p>
            </div>

            <div class="admin-stat-card">
                <h3>Всего предметов</h3>
                <p class="stat-number">${systemStats.totalItems}</p>
            </div>

            <div class="admin-stat-card">
                <h3>Активные вызовы</h3>
                <p class="stat-number">${systemStats.activeChallenges}</p>
            </div>

            <div class="admin-stat-card">
                <h3>Всего игр</h3>
                <p class="stat-number">${systemStats.totalGamesPlayed}</p>
            </div>
        </div>

        <div class="admin-sections">
            <div class="admin-section">
                <h3>Быстрые действия</h3>
                <div class="action-buttons">
                    <a href="${pageContext.request.contextPath}/admin/create-challenge" class="btn-primary">
                        Создать ежедневный вызов
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/items" class="btn-secondary">
                        Управление предметами
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/users" class="btn-secondary">
                        Управление пользователями
                    </a>
                </div>
            </div>

            <div class="admin-section">
                <h3>Последние вызовы</h3>
                <div class="recent-challenges">
                    <c:forEach var="challenge" items="${recentChallenges}">
                        <div class="challenge-item">
                            <span class="challenge-date">${challenge.date}</span>
                            <span class="challenge-blocks">${challenge.blocksCount} блоков</span>
                            <span class="challenge-status ${challenge.isActive ? 'active' : 'inactive'}">
                                    ${challenge.isActive ? 'Активен' : 'Неактивен'}
                            </span>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
    </main>

    <footer>
        <p>&copy; 2025 Buildle.gg - Not affiliated with Riot Games</p>
    </footer>
</div>
</body>
</html>
