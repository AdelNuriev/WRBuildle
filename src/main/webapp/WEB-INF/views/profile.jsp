<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Профиль - WR-Buildle.gg</title>
    <link rel="stylesheet" href="/css/layout.css">
    <link rel="stylesheet" href="/css/profile.css">
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

    <main class="profile-page">
        <h2>Профиль пользователя</h2>

        <div class="profile-header">
            <div class="profile-info">
                <h3>${currentUser.username}</h3>
                <p class="user-email">${currentUser.email}</p>
                <p class="user-level">Уровень: ${userStats.level}</p>
            </div>

            <div class="profile-stats">
                <div class="stat-card">
                    <h4>Всего игр</h4>
                    <p>${userStats.totalGames}</p>
                </div>
                <div class="stat-card">
                    <h4>Побед</h4>
                    <p>${userStats.gamesWon}</p>
                </div>
                <div class="stat-card">
                    <h4>Общий счет</h4>
                    <p>${userStats.totalScore}</p>
                </div>
                <div class="stat-card">
                    <h4>Ежедневная серия</h4>
                    <p>${userStats.dailyStreak} дней</p>
                </div>
            </div>
        </div>

        <div class="profile-customization">
            <h3>Настройки профиля</h3>

            <div class="customization-options">
                <div class="customization-section">
                    <h4>Иконка профиля</h4>
                    <div class="items-grid">
                        <c:forEach var="item" items="${inventory}">
                            <c:if test="${item.shopItem.type == 'ICON'}">
                                <div class="customization-item ${item.equipped ? 'equipped' : ''}">
                                    <img src="${item.shopItem.imageUrl}" alt="${item.shopItem.name}">
                                    <form action="/profile/equip" method="post">
                                        <input type="hidden" name="itemId" value="${item.id}">
                                        <button type="submit" class="btn-small">
                                                ${item.equipped ? 'Снято' : 'Надеть'}
                                        </button>
                                    </form>
                                </div>
                            </c:if>
                        </c:forEach>
                    </div>
                </div>

                <div class="customization-section">
                    <h4>Фон профиля</h4>
                    <div class="items-grid">
                        <c:forEach var="item" items="${inventory}">
                            <c:if test="${item.shopItem.type == 'BACKGROUND'}">
                                <div class="customization-item ${item.equipped ? 'equipped' : ''}">
                                    <img src="${item.shopItem.imageUrl}" alt="${item.shopItem.name}">
                                    <form action="/profile/equip" method="post">
                                        <input type="hidden" name="itemId" value="${item.id}">
                                        <button type="submit" class="btn-small">
                                                ${item.equipped ? 'Снято' : 'Надеть'}
                                        </button>
                                    </form>
                                </div>
                            </c:if>
                        </c:forEach>
                    </div>
                </div>
            </div>
        </div>
    </main>

    <footer>
        <p>&copy; 2025 WR-Buildle.gg - Не является собственностью Riot Games</p>
    </footer>
</div>
</body>
</html>
