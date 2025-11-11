<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Вход - WR-Buildle.gg</title>
    <link rel="stylesheet" href="/css/layout.css">
    <link rel="stylesheet" href="/css/auth.css">
</head>
<body>
<div class="container">
    <header>
        <h1>WR-Buildle.gg</h1>
        <p class="subtitle">Вход в аккаунт</p>
    </header>

    <main class="auth-container">
        <div class="auth-form">
            <h2>Вход</h2>

            <c:if test="${not empty error}">
                <div class="error-message">${error}</div>
            </c:if>

            <form action="/auth/login" method="post">
                <div class="form-group">
                    <label for="email">Email:</label>
                    <input type="email" id="email" name="email" required>
                </div>

                <div class="form-group">
                    <label for="password">Пароль:</label>
                    <input type="password" id="password" name="password" required>
                </div>

                <button type="submit" class="btn-primary">Войти</button>
            </form>

            <p class="auth-link">
                Нет аккаунта? <a href="/auth/register">Зарегистрироваться</a>
            </p>
        </div>
    </main>

    <footer>
        <p>&copy; 2025 WR-Buildle.gg - Не является собственностью Riot Games</p>
    </footer>
</div>
</body>
</html>