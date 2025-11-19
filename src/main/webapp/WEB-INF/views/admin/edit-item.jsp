<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Редактирование предмета - WR-Buildle.gg</title>
    <link rel="stylesheet" href="/css/layout.css">
    <link rel="stylesheet" href="/css/admin.css">
</head>
<body>
<div class="container">
    <header>
        <h1>WR-Buildle.gg</h1>
        <p class="subtitle">Редактирование предмета</p>
        <nav class="main-nav">
            <a href="/admin">Админ панель</a>
            <a href="/admin/items">Управление предметами</a>
            <a href="/dashboard">Главная</a>
            <a href="/auth/logout">Выйти</a>
        </nav>
    </header>

    <main class="admin-page">
        <h2>Редактирование предмета: ${item.name}</h2>

        <c:if test="${not empty error}">
            <div class="error-message">${error}</div>
        </c:if>

        <form action="/admin/update-item" method="post" class="create-form">
            <input type="hidden" name="itemId" value="${item.id}">

            <div class="form-row">
                <div class="form-group">
                    <label>Название:</label>
                    <input type="text" name="name" value="${item.name}" required>
                </div>
                <div class="form-group">
                    <label>Редкость:</label>
                    <select name="rarity" required>
                        <option value="COMMON" ${item.rarity == 'COMMON' ? 'selected' : ''}>Обычный</option>
                        <option value="BOOT" ${item.rarity == 'BOOT' ? 'selected' : ''}>Сапоги</option>
                        <option value="EPIC" ${item.rarity == 'EPIC' ? 'selected' : ''}>Эпический</option>
                        <option value="MYTHICAL" ${item.rarity == 'MYTHICAL' ? 'selected' : ''}>Мифический</option>
                        <option value="LEGENDARY" ${item.rarity == 'LEGENDARY' ? 'selected' : ''}>Легендарный</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>Тип способности:</label>
                    <select name="type" required>
                        <option value="ACTIVE">Активный</option>
                        <option value="PASSIVE">Пассивный</option>
                    </select>
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">mv
                    <label>Стоимость:</label>
                    <input type="number" name="cost" value="${item.cost}" min="0" max="5000" required>
                </div>
                <div class="form-group">
                    <label>URL иконки:</label>
                    <input type="text" name="iconUrl" value="${item.iconUrl}" required>
                </div>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn-primary">Сохранить изменения</button>
                <a href="/admin/items" class="btn-secondary">Отмена</a>
            </div>
        </form>
    </main>
</div>
</body>
</html>
