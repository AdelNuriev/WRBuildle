<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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
            <a href="/profile" class="active">Профиль</a>
            <a href="/statistics">Статистика</a>
            <c:if test="${currentUser.role == 'ADMIN'}">
                <a href="/admin">Админ панель</a>
            </c:if>
            <a href="/auth/logout">Выйти</a>
        </nav>
    </header>

    <main class="profile-page">
        <h2>Профиль пользователя</h2>

        <c:if test="${not empty success}">
            <div class="success-message">
                    ${success}
            </div>
        </c:if>

        <c:if test="${not empty error}">
            <div class="error-message">
                    ${error}
            </div>
        </c:if>

        <div class="profile-header">
            <div class="profile-info">
                <h3>${currentUser.username}</h3>
                <p class="user-email">${currentUser.email}</p>
                <p class="user-level">Уровень: ${currentUser.level}</p>
                <p class="user-coins">Монеты: ${currentUser.coins} 🪙</p>
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
                    <h4>Процент побед</h4>
                    <p>
                        <c:choose>
                            <c:when test="${userStats.totalGames > 0}">
                                <fmt:formatNumber value="${Math.round((userStats.gamesWon * 100) / userStats.totalGames)}" pattern="#.##"/>%
                            </c:when>
                            <c:otherwise>
                                0%
                            </c:otherwise>
                        </c:choose>
                    </p>
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
                <div class="equipped-section">
                    <h4>Сейчас экипировано</h4>
                    <div class="equipped-items">
                        <c:forEach var="equipped" items="${equippedItems}">
                            <c:forEach var="item" items="${inventory}">
                                <c:if test="${item.purchaseId == equipped.id}">
                                    <div class="equipped-item">
                                        <img src="${item.shopItem.imageUrl}" alt="${item.shopItem.name}"
                                             onerror="this.src='/images/system/default-item.png'">
                                        <span>${item.shopItem.name}</span>
                                        <small>${item.shopItem.type.displayName}</small>
                                    </div>
                                </c:if>
                            </c:forEach>
                        </c:forEach>
                        <c:if test="${empty equippedItems}">
                            <p class="no-items">Нет экипированных предметов</p>
                        </c:if>
                    </div>
                </div>

                <div class="customization-section">
                    <h4>Иконки профиля</h4>
                    <div class="items-grid">
                        <c:forEach var="item" items="${inventory}">
                            <c:if test="${item.shopItem.type == 'ICON'}">
                                <div class="customization-item ${item.equipped ? 'equipped' : ''}">
                                    <img src="${item.shopItem.imageUrl}" alt="${item.shopItem.name}"
                                         onerror="this.src='/images/system/default-item.png'">
                                    <form action="/profile/equip" method="post">
                                        <input type="hidden" name="itemId" value="${item.purchaseId}">
                                        <button type="submit" class="btn-small ${item.equipped ? 'btn-equipped' : 'btn-unequipped'}">
                                            <c:choose>
                                                <c:when test="${item.equipped}">
                                                    ✓ Экипировано
                                                </c:when>
                                                <c:otherwise>
                                                    Надеть
                                                </c:otherwise>
                                            </c:choose>
                                        </button>
                                    </form>
                                </div>
                            </c:if>
                        </c:forEach>
                        <c:if test="${empty inventory}">
                            <div class="no-items-message">
                                <p>У вас пока нет иконок профиля</p>
                                <a href="/shop" class="btn-primary">Посетить магазин</a>
                            </div>
                        </c:if>
                    </div>
                </div>

                <div class="customization-section">
                    <h4>Фоны профиля</h4>
                    <div class="items-grid">
                        <c:forEach var="item" items="${inventory}">
                            <c:if test="${item.shopItem.type == 'BACKGROUND'}">
                                <div class="customization-item ${item.equipped ? 'equipped' : ''}">
                                    <img src="${item.shopItem.imageUrl}" alt="${item.shopItem.name}"
                                         onerror="this.src='/images/system/default-item.png'">
                                    <form action="/profile/equip" method="post">
                                        <input type="hidden" name="itemId" value="${item.purchaseId}">
                                        <button type="submit" class="btn-small ${item.equipped ? 'btn-equipped' : 'btn-unequipped'}">
                                            <c:choose>
                                                <c:when test="${item.equipped}">
                                                    ✓ Экипировано
                                                </c:when>
                                                <c:otherwise>
                                                    Надеть
                                                </c:otherwise>
                                            </c:choose>
                                        </button>
                                    </form>
                                </div>
                            </c:if>
                        </c:forEach>
                        <c:if test="${empty inventory}">
                            <div class="no-items-message">
                                <p>У вас пока нет фонов профиля</p>
                                <a href="/shop" class="btn-primary">Посетить магазин</a>
                            </div>
                        </c:if>
                    </div>
                </div>

                <div class="customization-section">
                    <h4>Рамки профиля</h4>
                    <div class="items-grid">
                        <c:forEach var="item" items="${inventory}">
                            <c:if test="${item.shopItem.type == 'BORDER'}">
                                <div class="customization-item ${item.equipped ? 'equipped' : ''}">
                                    <img src="${item.shopItem.imageUrl}" alt="${item.shopItem.name}"
                                         onerror="this.src='/images/system/default-item.png'">
                                    <form action="/profile/equip" method="post">
                                        <input type="hidden" name="itemId" value="${item.purchaseId}">
                                        <button type="submit" class="btn-small ${item.equipped ? 'btn-equipped' : 'btn-unequipped'}">
                                            <c:choose>
                                                <c:when test="${item.equipped}">
                                                    ✓ Экипировано
                                                </c:when>
                                                <c:otherwise>
                                                    Надеть
                                                </c:otherwise>
                                            </c:choose>
                                        </button>
                                    </form>
                                </div>
                            </c:if>
                        </c:forEach>
                        <c:if test="${empty inventory}">
                            <div class="no-items-message">
                                <p>У вас пока нет рамок профиля</p>
                                <a href="/shop" class="btn-primary">Посетить магазин</a>
                            </div>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>

        <div class="inventory-section" id="inventory">
            <h3>Весь инвентарь</h3>
            <div class="inventory-stats">
                <p>Всего предметов: ${fn:length(inventory)}</p>
            </div>
            <div class="inventory-grid">
                <c:forEach var="item" items="${inventory}">
                    <div class="inventory-item ${item.equipped ? 'equipped' : ''}">
                        <img src="${item.shopItem.imageUrl}" alt="${item.shopItem.name}"
                             onerror="this.src='/images/shop/default-item.png'">
                        <div class="item-info">
                            <h5>${item.shopItem.name}</h5>
                            <p class="item-type">${item.shopItem.type.displayName}</p>
                            <p class="item-rarity ${item.shopItem.rarity}">${item.shopItem.rarity.displayName}</p>
                            <p class="purchase-date">Куплено: ${item.purchasedAt}</p>
                            <c:if test="${item.equipped}">
                                <span class="equipped-badge">Экипировано</span>
                            </c:if>
                        </div>
                        <form action="/profile/equip" method="post">
                            <input type="hidden" name="itemId" value="${item.purchaseId}">
                            <button type="submit" class="btn-small ${item.equipped ? 'btn-equipped' : 'btn-unequipped'}">
                                <c:choose>
                                    <c:when test="${item.equipped}">
                                        Снять
                                    </c:when>
                                    <c:otherwise>
                                        Надеть
                                    </c:otherwise>
                                </c:choose>
                            </button>
                        </form>
                    </div>
                </c:forEach>
                <c:if test="${empty inventory}">
                    <div class="no-items-message full-width">
                        <p>Ваш инвентарь пуст</p>
                        <a href="/shop" class="btn-primary">Посетить магазин</a>
                    </div>
                </c:if>
            </div>
        </div>
    </main>

    <footer>
        <p>&copy; 2025 WR-Buildle.gg - Не является собственностью Riot Games</p>
    </footer>
</div>
</body>
</html>