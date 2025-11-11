<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Создание вызова - WR-Buildle.gg</title>
    <link rel="stylesheet" href="/css/layout.css">
    <link rel="stylesheet" href="/css/admin.css">
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

    <main class="admin-page">
        <h2>Создание ежедневного вызова</h2>

        <c:if test="${not empty error}">
            <div class="error-message">${error}</div>
        </c:if>

        <form action="/admin/create-daily-challenge" method="post" class="challenge-form">
            <div class="form-group">
                <label>Дата вызова:</label>
                <input type="date" name="challengeDate" value="${currentDate}" required>
            </div>

            <div class="challenge-blocks">
                <c:forEach var="blockType" items="<%=ru.itis.wr.entities.BlockType.values()%>">
                    <div class="challenge-block-admin">
                        <h3>${blockType.displayName}</h3>
                        <p class="block-description">${blockType.context}</p>

                        <div class="block-config">
                            <div class="form-group">
                                <label>Целевой предмет:</label>
                                <select name="${blockType.name().toLowerCase()}TargetItemId" required>
                                    <option value="">Выберите предмет</option>
                                    <c:forEach var="item" items="${items}">
                                        <option value="${item.id}">${item.name} (${item.cost}g)</option>
                                    </c:forEach>
                                </select>
                            </div>

                            <c:if test="${blockType == 'MISSING' || blockType == 'IMPOSTER'}">
                                <div class="form-group">
                                    <label>Дополнительный предмет:</label>
                                    <select name="${blockType.name().toLowerCase()}ExtraItemId" required>
                                        <option value="">Выберите предмет</option>
                                        <c:forEach var="item" items="${items}">
                                            <option value="${item.id}">${item.name} (${item.cost}g)</option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </c:if>
                        </div>
                    </div>
                </c:forEach>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn-primary">Создать ежедневный вызов</button>
                <a href="/admin/challenges" class="btn-secondary">Отмена</a>
            </div>
        </form>
    </main>

    <footer>
        <p>&copy; 2025 WR-Buildle.gg - Не является собственностью Riot Games</p>
    </footer>
</div>
</body>
</html>
