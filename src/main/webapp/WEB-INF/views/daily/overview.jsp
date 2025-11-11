<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ежедневный вызов - WR-Buildle.gg</title>
    <link rel="stylesheet" href="/css/layout.css">
    <link rel="stylesheet" href="/css/challenge.css">
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

    <main class="daily-overview">
        <h2>Ежедневный вызов</h2>
        <p class="subtitle">${challenge.challengeDate}</p>

        <div class="challenge-grid">
            <div class="challenge-block" onclick="location.href='/daily/icon'">
                <div class="block-header">
                    <div class="block-icon">🖼️</div>
                    <h3>Иконка</h3>
                </div>
                <p>Угадайте предмет по иконке</p>
                <c:forEach var="result" items="${userResults}">
                    <c:if test="${result.blockType == 'ICON'}">
                        <div class="block-status ${result.completed ? 'completed' : 'in-progress'}">
                                ${result.completed ? '✅ Завершено' : '🔄 В процессе'}
                        </div>
                    </c:if>
                </c:forEach>
            </div>

            <div class="challenge-block" onclick="location.href='/daily/classic'">
                <div class="block-header">
                    <div class="block-icon">🏗️</div>
                    <h3>Классика</h3>
                </div>
                <p>Соберите дерево предметов</p>
                <c:forEach var="result" items="${userResults}">
                    <c:if test="${result.blockType == 'CLASSIC'}">
                        <div class="block-status ${result.completed ? 'completed' : 'in-progress'}">
                                ${result.completed ? '✅ Завершено' : '🔄 В процессе'}
                        </div>
                    </c:if>
                </c:forEach>
            </div>

            <div class="challenge-block" onclick="location.href='/daily/attributes'">
                <div class="block-header">
                    <div class="block-icon">📊</div>
                    <h3>Атрибуты</h3>
                </div>
                <p>Угадайте по характеристикам</p>
                <c:forEach var="result" items="${userResults}">
                    <c:if test="${result.blockType == 'ATTRIBUTES'}">
                        <div class="block-status ${result.completed ? 'completed' : 'in-progress'}">
                                ${result.completed ? '✅ Завершено' : '🔄 В процессе'}
                        </div>
                    </c:if>
                </c:forEach>
            </div>

            <div class="challenge-block" onclick="location.href='/daily/missing'">
                <div class="block-header">
                    <div class="block-icon">❓</div>
                    <h3>Пропуск</h3>
                </div>
                <p>Найдите недостающий предмет</p>
                <c:forEach var="result" items="${userResults}">
                    <c:if test="${result.blockType == 'MISSING'}">
                        <div class="block-status ${result.completed ? 'completed' : 'in-progress'}">
                                ${result.completed ? '✅ Завершено' : '🔄 В процессе'}
                        </div>
                    </c:if>
                </c:forEach>
            </div>

            <div class="challenge-block" onclick="location.href='/daily/imposter'">
                <div class="block-header">
                    <div class="block-icon">👤</div>
                    <h3>Предатель</h3>
                </div>
                <p>Найдите лишний предмет</p>
                <c:forEach var="result" items="${userResults}">
                    <c:if test="${result.blockType == 'IMPOSTER'}">
                        <div class="block-status ${result.completed ? 'completed' : 'in-progress'}">
                                ${result.completed ? '✅ Завершено' : '🔄 В процессе'}
                        </div>
                    </c:if>
                </c:forEach>
            </div>

            <div class="challenge-block" onclick="location.href='/daily/cost'">
                <div class="block-header">
                    <div class="block-icon">💰</div>
                    <h3>Стоимость</h3>
                </div>
                <p>Угадайте стоимость предмета</p>
                <c:forEach var="result" items="${userResults}">
                    <c:if test="${result.blockType == 'COST'}">
                        <div class="block-status ${result.completed ? 'completed' : 'in-progress'}">
                                ${result.completed ? '✅ Завершено' : '🔄 В процессе'}
                        </div>
                    </c:if>
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
