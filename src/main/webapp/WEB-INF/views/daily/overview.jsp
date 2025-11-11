<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ежедневный вызов - Buildle.gg</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/challenge.css">
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

    <main class="daily-overview">
        <h2>Ежедневный вызов</h2>
        <p class="subtitle">${challenge.challengeDate}</p>

        <div class="challenge-grid">
            <div class="challenge-block" onclick="location.href='${pageContext.request.contextPath}/daily/icon'">
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

            <div class="challenge-block" onclick="location.href='${pageContext.request.contextPath}/daily/classic'">
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

            <div class="challenge-block" onclick="location.href='${pageContext.request.contextPath}/daily/attributes'">
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

            <div class="challenge-block" onclick="location.href='${pageContext.request.contextPath}/daily/missing'">
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

            <div class="challenge-block" onclick="location.href='${pageContext.request.contextPath}/daily/imposter'">
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

            <div class="challenge-block" onclick="location.href='${pageContext.request.contextPath}/daily/cost'">
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
        <p>&copy; 2025 Buildle.gg - Not affiliated with Riot Games</p>
    </footer>
</div>
</body>
</html>
