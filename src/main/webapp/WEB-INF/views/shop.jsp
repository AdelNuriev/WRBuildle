<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Магазин - WR-Buildle.gg</title>
    <link rel="stylesheet" href="/css/layout.css">
    <link rel="stylesheet" href="/css/shop.css">
    <script src="/js/shop.js" defer></script>
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
            <a href="/shop" class="active">Магазин</a>
            <a href="/profile">Профиль</a>
            <a href="/statistics">Статистика</a>
            <c:if test="${currentUser.role == 'ADMIN'}">
                <a href="/admin">Админ панель</a>
            </c:if>
            <a href="/auth/logout">Выйти</a>
        </nav>
    </header>

    <main class="shop-page">
        <h2>Магазин</h2>

        <div class="shop-header">
            <div class="user-coins">
                <h3>Ваши монеты: <span id="userCoins">${currentUser.coins}</span> 🪙</h3>
            </div>
            <div class="shop-actions">
                <a href="/profile#inventory" class="btn-secondary">Мой инвентарь</a>
            </div>
        </div>

        <c:if test="${not empty success}">
            <div class="success-message" id="successMessage">
                    ${success}
                <button class="close-message" onclick="closeMessage('successMessage')">×</button>
            </div>
        </c:if>

        <c:if test="${not empty error}">
            <div class="error-message" id="errorMessage">
                    ${error}
                <button class="close-message" onclick="closeMessage('errorMessage')">×</button>
            </div>
        </c:if>

        <div class="shop-controls">
            <div class="search-box">
                <input type="text" id="searchInput" placeholder="Поиск предметов..." onkeyup="searchItems()">
            </div>
            <div class="sort-options">
                <select id="sortSelect" onchange="sortItems()">
                    <option value="price_asc">Цена (по возрастанию)</option>
                    <option value="price_desc">Цена (по убыванию)</option>
                    <option value="name_asc">Название (А-Я)</option>
                    <option value="name_desc">Название (Я-А)</option>
                    <option value="rarity">Редкость</option>
                </select>
            </div>
        </div>

        <div class="shop-categories">
            <button class="category-btn active" onclick="filterItems('all')">Все предметы</button>
            <button class="category-btn" onclick="filterItems('ICON')">Иконки</button>
            <button class="category-btn" onclick="filterItems('BACKGROUND')">Фоны</button>
            <button class="category-btn" onclick="filterItems('BORDER')">Рамки</button>
            <button class="category-btn" onclick="filterItems('FONT')">Шрифты</button>
        </div>

        <div class="shop-stats">
            <div class="stats-item">
                <span class="stats-label">Всего предметов:</span>
                <span class="stats-value" id="totalItems">${fn:length(shopItems)}</span>
            </div>
            <div class="stats-item">
                <span class="stats-label">Показано:</span>
                <span class="stats-value" id="shownItems">${fn:length(shopItems)}</span>
            </div>
            <div class="stats-item">
                <span class="stats-label">Куплено:</span>
                <span class="stats-value" id="ownedItems">${fn:length(userInventory)}</span>
            </div>
        </div>

        <div class="shop-items" id="shopItemsContainer">
            <c:forEach var="item" items="${shopItems}">
                <c:set var="isOwned" value="false" />
                <c:forEach var="purchase" items="${userInventory}">
                    <c:if test="${purchase.shopItemId == item.id}">
                        <c:set var="isOwned" value="true" />
                    </c:if>
                </c:forEach>

                <div class="shop-item" data-type="${item.type}" data-name="${fn:toLowerCase(item.name)}"
                     data-price="${item.price}" data-rarity="${item.rarity}">
                    <div class="item-image">
                        <img src="${item.imageUrl}" alt="${item.name}">
                        <div class="item-rarity ${item.rarity}">${item.rarity.displayName}</div>
                        <c:if test="${isOwned}">
                            <div class="owned-overlay">Куплено</div>
                        </c:if>
                    </div>

                    <div class="item-info">
                        <h4>${item.name}</h4>
                        <p class="item-type">${item.type.displayName}</p>

                        <div class="item-price">
                            <span class="price-amount">${item.price} 🪙</span>
                            <c:if test="${isOwned}">
                                <span class="owned-badge">✓ В инвентаре</span>
                            </c:if>
                        </div>
                    </div>

                    <div class="item-actions">
                        <c:choose>
                            <c:when test="${isOwned}">
                                <button class="btn-owned" disabled>
                                    <span>✓ Куплено</span>
                                </button>
                                <a href="/profile#inventory" class="btn-equip">Экипировать</a>
                            </c:when>
                            <c:when test="${currentUser.coins >= item.price}">
                                <form action="/shop/purchase" method="post" class="purchase-form">
                                    <input type="hidden" name="itemId" value="${item.id}">
                                    <button type="submit" class="btn-purchase"
                                            onclick="return confirmPurchase(${item.price}, '${item.name}')">
                                        Купить за ${item.price} 🪙
                                    </button>
                                </form>
                            </c:when>
                            <c:otherwise>
                                <button class="btn-no-coins" disabled>
                                    Недостаточно монет
                                </button>
                                <div class="coins-needed">
                                    Нужно ещё ${item.price - currentUser.coins} 🪙
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </c:forEach>

            <c:if test="${empty shopItems}">
                <div class="no-items">
                    <div class="no-items-icon">🛒</div>
                    <h3>Магазин пуст</h3>
                    <p>В настоящее время нет доступных предметов для покупки.</p>
                </div>
            </c:if>
        </div>

        <div class="shop-footer">
            <div class="pagination" id="pagination">
            </div>
        </div>
    </main>

    <div id="purchaseModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h3>Подтверждение покупки</h3>
                <button class="close-modal" onclick="closeModal('purchaseModal')">×</button>
            </div>
            <div class="modal-body">
                <p id="purchaseMessage"></p>
                <div class="modal-item-info">
                    <img id="modalItemImage" src="" alt="">
                    <div>
                        <h4 id="modalItemName"></h4>
                        <p id="modalItemType"></p>
                        <p id="modalItemPrice"></p>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button class="btn-secondary" onclick="closeModal('purchaseModal')">Отмена</button>
                <form id="confirmPurchaseForm" method="post" action="/shop/purchase">
                    <input type="hidden" name="itemId" id="modalItemId">
                    <button type="submit" class="btn-primary">Подтвердить покупку</button>
                </form>
            </div>
        </div>
    </div>

    <footer>
        <p>&copy; 2025 WR-Buildle.gg - Не является собственностью Riot Games</p>
    </footer>
</div>

<script>
    function filterItems(type) {
        if (window.shopManager) {
            window.shopManager.filterItems(type);
        }
    }

    function searchItems() {
        if (window.shopManager) {
            const searchTerm = document.getElementById('searchInput').value;
            window.shopManager.searchItems(searchTerm);
        }
    }

    function sortItems() {
        if (window.shopManager) {
            window.shopManager.currentSort = document.getElementById('sortSelect').value;
            window.shopManager.applySorting();
        }
    }

    function confirmPurchase(price, name) {
        if (!window.shopManager) return false;

        const itemElement = event.target.closest('.shop-item');
        if (!itemElement) return false;

        const itemIdInput = itemElement.querySelector('input[name="itemId"]');
        const imageElement = itemElement.querySelector('img');

        if (!itemIdInput || !imageElement) return false;

        const itemId = itemIdInput.value;
        const imageUrl = imageElement.src;
        const type = itemElement.dataset.type;

        return window.shopManager.confirmPurchase(price, name, itemId, imageUrl, type);
    }

    function closeModal(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.style.display = 'none';
        }
    }

    function closeMessage(messageId) {
        const message = document.getElementById(messageId);
        if (message) {
            message.style.display = 'none';
        }
    }

    document.addEventListener('DOMContentLoaded', function() {
        window.shopManager = new ShopManager();

        setTimeout(() => {
            const messages = document.querySelectorAll('.success-message, .error-message');
            messages.forEach(msg => {
                if (msg.style.display !== 'none') {
                    msg.style.display = 'none';
                }
            });
        }, 5000);
    });
</script>
</body>
</html>
