<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ошибка - WR-Buildle.gg</title>
    <link rel="stylesheet" href="/css/layout.css">
    <link rel="stylesheet" href="/css/error.css">
</head>
<body>
<div class="container">
    <header>
        <h1>WR-Buildle.gg</h1>
        <p class="subtitle">Произошла ошибка</p>
    </header>

    <main class="error-container">
        <div class="error-message">
            <h2>Что-то пошло не так</h2>
            <c:if test="${not empty error}">
                <p>${error}</p>
            </c:if>
            <c:if test="${empty error}">
                <p>Произошла непредвиденная ошибка. Пожалуйста, попробуйте позже.</p>
            </c:if>
            <a href="/dashboard" class="btn-primary">Вернуться на главную</a>
        </div>
    </main>

    <footer>
        <p>&copy; 2025 WR-Buildle.gg - Не является собственностью Riot Games</p>
    </footer>
</div>
</body>
</html>
