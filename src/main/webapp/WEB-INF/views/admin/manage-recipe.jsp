<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Управление сборкой - WR-Buildle.gg</title>
    <link rel="stylesheet" href="/css/layout.css">
    <link rel="stylesheet" href="/css/admin.css">
    <style>
        .recipe-tree-container {
            border: 2px solid #c8aa6e;
            border-radius: 8px;
            padding: 20px;
            background: rgba(20, 20, 20, 0.8);
            margin: 20px 0;
            max-height: 600px;
            overflow-y: auto;
        }

        .recipe-tree {
            display: flex;
            flex-direction: column;
            align-items: center;
        }

        .tree-branch {
            display: flex;
            flex-direction: column;
            align-items: center;
            margin: 10px 0;
            position: relative;
        }

        .branch-children {
            display: flex;
            flex-wrap: wrap;
            justify-content: center;
            gap: 20px;
            margin-top: 20px;
            padding-top: 20px;
            position: relative;
        }

        .branch-children::before {
            content: '';
            position: absolute;
            top: 0;
            left: 50%;
            width: 80%;
            height: 2px;
            background: #c8aa6e;
            transform: translateX(-50%);
        }

        .tree-node {
            display: flex;
            flex-direction: column;
            align-items: center;
            padding: 10px;
            border: 2px solid #c8aa6e;
            border-radius: 8px;
            background: rgba(40, 40, 40, 0.9);
            cursor: pointer;
            transition: all 0.3s ease;
            min-width: 100px;
            position: relative;
        }

        .tree-node:hover {
            border-color: #f0e6d2;
            background: rgba(60, 60, 60, 0.9);
        }

        .tree-node.selected {
            border-color: #00ff00;
            box-shadow: 0 0 10px rgba(0, 255, 0, 0.5);
        }

        .tree-node.empty {
            border: 2px dashed #c8aa6e;
            background: rgba(30, 30, 30, 0.5);
            color: #c8aa6e;
            font-size: 24px;
            font-weight: bold;
        }

        .tree-node.empty:hover {
            border-color: #f0e6d2;
            background: rgba(40, 40, 40, 0.7);
        }

        .node-icon {
            width: 48px;
            height: 48px;
            object-fit: contain;
        }

        .node-name {
            margin-top: 5px;
            font-size: 12px;
            text-align: center;
            color: #f0e6d2;
            max-width: 80px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }

        .branch-actions {
            position: absolute;
            top: -10px;
            right: -10px;
        }

        .btn-remove {
            background: #e74c3c;
            color: white;
            border: none;
            border-radius: 50%;
            width: 24px;
            height: 24px;
            cursor: pointer;
            font-size: 16px;
            line-height: 1;
        }

        .btn-remove:hover {
            background: #c0392b;
        }

        .components-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
            gap: 15px;
            margin-top: 20px;
        }

        .component-card {
            border: 2px solid #c8aa6e;
            border-radius: 8px;
            padding: 15px;
            background: rgba(40, 40, 40, 0.9);
            cursor: pointer;
            transition: all 0.3s ease;
            text-align: center;
        }

        .component-card:hover {
            border-color: #f0e6d2;
            background: rgba(60, 60, 60, 0.9);
        }

        .component-card.selected {
            border-color: #00ff00;
            box-shadow: 0 0 10px rgba(0, 255, 0, 0.5);
        }

        .component-icon {
            width: 48px;
            height: 48px;
            object-fit: contain;
            margin-bottom: 8px;
        }

        .component-name {
            font-size: 12px;
            color: #f0e6d2;
            margin-bottom: 5px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }

        .component-cost {
            font-size: 11px;
            color: #c8aa6e;
            font-weight: bold;
        }

        .tree-controls {
            margin-bottom: 20px;
        }

        .selection-info {
            padding: 10px;
            background: rgba(200, 170, 110, 0.1);
            border-radius: 5px;
            margin: 10px 0;
            text-align: center;
        }

        .quantity-badge {
            position: absolute;
            top: -5px;
            right: -5px;
            background: #c8aa6e;
            color: #000;
            border-radius: 50%;
            width: 20px;
            height: 20px;
            font-size: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: bold;
        }

        .tree-node {
            position: relative;
        }
    </style>
</head>
<body>
<div class="container">
    <header>
        <h1>WR-Buildle.gg</h1>
        <p class="subtitle">Управление сборкой предмета</p>
        <nav class="main-nav">
            <a href="/admin">Админ панель</a>
            <a href="/admin/items">Управление предметами</a>
            <a href="/dashboard">Главная</a>
            <a href="/auth/logout">Выйти</a>
        </nav>
    </header>

    <main class="admin-page">
        <h2>Управление сборкой: ${item.name}</h2>

        <div class="item-header">
            <img src="${item.iconUrl}" alt="${item.name}" class="item-icon-admin">
            <div class="item-info">
                <h3>${item.name}</h3>
                <p class="item-rarity ${item.rarity}">${item.rarity.displayName}</p>
                <p class="item-cost">${item.cost} золота</p>
            </div>
        </div>

        <c:if test="${not empty success}">
            <div class="success-message">${success}</div>
        </c:if>

        <c:if test="${not empty error}">
            <div class="error-message">${error}</div>
        </c:if>

        <div class="recipe-tree-section">
            <h3>Схема сборки (полное дерево)</h3>
            <div class="tree-controls">
                <button type="button" class="btn-secondary" id="addRootBtn">
                    Добавить корневой компонент
                </button>
                <button type="button" class="btn-primary" id="saveTreeBtn">
                    Сохранить дерево сборки
                </button>
            </div>
            <div class="recipe-tree-container">
                <div class="recipe-tree" id="recipeTree">
                    <div class="tree-branch root-branch">
                        <div class="tree-node root-node selected" data-item-id="${item.id}">
                            <img src="${item.iconUrl}" alt="${item.name}" class="node-icon">
                            <span class="node-name">${item.name}</span>
                        </div>
                        <div class="branch-children" id="rootComponents">
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="component-selection-section">
            <h3>Выбор компонентов</h3>
            <div class="selection-controls">
                <input type="text" id="searchInput" placeholder="Поиск предметов..."
                       class="form-control">
                <div class="selection-info" id="selectionInfo">
                    Выберите узел в дереве сверху для добавления компонента
                </div>
            </div>

            <div class="components-grid" id="componentsGrid">
            </div>
        </div>
    </main>
</div>

<script>
    window.CURRENT_ITEM = {
        id: ${item.id},
        name: '<c:out value="${item.name}" />',
        iconUrl: '<c:out value="${item.iconUrl}" />',
        cost: ${item.cost}
    };
</script>

<script src="/js/manage-recipe.js"></script>

<script>
    document.addEventListener('DOMContentLoaded', function() {
        console.log('DOM loaded, initializing recipe tree...');

        const addRootBtn = document.getElementById('addRootBtn');
        const saveTreeBtn = document.getElementById('saveTreeBtn');
        const searchInput = document.getElementById('searchInput');

        if (addRootBtn) {
            addRootBtn.addEventListener('click', function() {
                console.log('Add root button clicked');
                if (typeof window.addRootComponent === 'function') {
                    window.addRootComponent();
                } else {
                    console.error('addRootComponent function not found');
                }
            });
        }

        if (saveTreeBtn) {
            saveTreeBtn.addEventListener('click', function() {
                console.log('Save tree button clicked');
                if (typeof window.saveRecipeTree === 'function') {
                    window.saveRecipeTree();
                } else {
                    console.error('saveRecipeTree function not found');
                }
            });
        }

        if (searchInput) {
            searchInput.addEventListener('input', function() {
                if (typeof window.searchItems === 'function') {
                    window.searchItems();
                } else {
                    console.error('searchItems function not found');
                }
            });
        }

        if (typeof window.initializeRecipeTree === 'function') {
            console.log('Initializing recipe tree...');
            window.initializeRecipeTree();
        } else {
            console.error('initializeRecipeTree function not found');
        }
    });
</script>
</body>
</html>
