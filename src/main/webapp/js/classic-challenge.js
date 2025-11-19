let allItems = [];
let guessedItems = new Set();
let guessHistory = [];

window.searchItems = searchItems;
window.selectItem = selectItem;

document.addEventListener('DOMContentLoaded', function() {
    loadAllItems();
    initializeChallengeTree();
});

function loadAllItems() {
    console.log('Loading all items...');
    fetch('/daily/api/items')
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok: ' + response.status);
            }
            return response.json();
        })
        .then(items => {
            console.log('Loaded items:', items);
            allItems = items.map(item => ({
                ...item,
                id: safeParseInt(item.id)
            }));
            renderComponentsGrid(allItems);
        })
        .catch(error => {
            console.error('Error loading items:', error);
            showNotification('Ошибка загрузки предметов: ' + error.message, 'error');
        });
}

function initializeChallengeTree() {
    console.log('Initializing challenge tree');

    const treeDataScript = document.getElementById('treeData');
    if (!treeDataScript) {
        console.error('Tree data script element not found');
        showNotification('Ошибка: элемент с данными дерева не найден', 'error');
        return;
    }

    const treeDataText = treeDataScript.textContent || treeDataScript.innerText;
    console.log('Raw tree data text length:', treeDataText ? treeDataText.length : 'null');

    if (!treeDataText || treeDataText.trim() === '') {
        console.error('Tree data text is empty');
        showNotification('Данные дерева сборки отсутствуют', 'error');
        return;
    }

    try {
        const cleanedText = treeDataText.trim();
        const treeData = JSON.parse(cleanedText);
        console.log('Successfully parsed tree data:', treeData);
        renderChallengeTree(treeData);
    } catch (error) {
        console.error('Error parsing tree data:', error);
        console.error('Problematic JSON (first 500 chars):', treeDataText ? treeDataText.substring(0, 500) : 'null');

        showNotification('Ошибка загрузки дерева сборки: ' + error.message, 'error');
        renderFallbackTree();
    }
}

function renderChallengeTree(treeData) {
    const treeContainer = document.getElementById('treeContainer');
    if (!treeContainer) {
        console.error('Tree container not found');
        return;
    }

    treeContainer.innerHTML = '';

    if (!treeData || typeof treeData !== 'object') {
        console.error('Invalid tree data structure:', treeData);
        renderFallbackTree();
        return;
    }

    if (treeData.error) {
        console.error('Tree data contains error:', treeData.error);
        renderFallbackTree();
        return;
    }

    if (!treeData.item) {
        console.error('Tree data missing item property:', treeData);
        renderFallbackTree();
        return;
    }

    try {
        const rootNode = createTreeNode(treeData.item, true);
        treeContainer.appendChild(rootNode);

        const componentsRow = document.createElement('div');
        componentsRow.className = 'components-row';
        componentsRow.style.cssText = `
            position: absolute;
            left: 50%;
            transform: translateX(-50%);
            width: 2px;
            height: 20px;
            background: #c8aa6e;
        `;

        const verticalConnector = document.createElement('div');
        verticalConnector.className = 'vertical-connector';
        verticalConnector.style.cssText = `
            position: absolute;
            top: -20px;
            left: 50%;
            transform: translateX(-50%);
            width: 2px;
            height: 20px;
            background: #c8aa6e;
        `;

        const horizontalConnector = document.createElement('div');
        horizontalConnector.className = 'horizontal-connector';
        horizontalConnector.style.cssText = `
            position: absolute;
            top: -20px;
            left: 50%;
            right: 50%;
            height: 2px;
            background: #c8aa6e;
            width: calc(100% - 80px);
            margin: 0 40px;
        `;

        componentsRow.appendChild(verticalConnector);
        componentsRow.appendChild(horizontalConnector);

        if (treeData.components && treeData.components.length > 0) {
            treeData.components.forEach((component) => {
                const componentBranch = createComponentBranch(component);
                componentsRow.appendChild(componentBranch);
            });
        }

        treeContainer.appendChild(componentsRow);
        console.log('Tree rendered successfully');

    } catch (error) {
        console.error('Error rendering challenge tree:', error);
        renderFallbackTree();
    }
}

function renderFallbackTree() {
    const treeContainer = document.getElementById('treeContainer');
    if (!treeContainer) return;

    treeContainer.innerHTML = `
        <div style="text-align: center;">
            <div class="tree-node challenge-node root-node" style="margin: 0 auto;">
                <div class="question-mark">?</div>
                <div class="node-content hidden">
                    <img src="" alt="Неизвестный предмет" class="node-icon">
                </div>
            </div>
        </div>
    `;
}

function createComponentBranch(component) {
    const branchContainer = document.createElement('div');
    branchContainer.className = 'component-branch';
    branchContainer.style.cssText = `
        display: flex;
        flex-direction: column;
        align-items: center;
        position: relative;
    `;

    const count = component.count || 1;

    const duplicatesContainer = document.createElement('div');
    duplicatesContainer.className = 'duplicates-container';
    duplicatesContainer.style.cssText = `
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 10px;
    `;

    const connector = document.createElement('div');
    connector.className = 'branch-connector';
    connector.style.cssText = `
        width: 2px;
        height: 20px;
        background: #c8aa6e;
        margin-bottom: 10px;
    `;

    duplicatesContainer.appendChild(connector);

    for (let i = 0; i < count; i++) {
        const componentNode = createTreeNode(component.item, false);

        if (count > 1) {
            const countBadge = document.createElement('div');
            countBadge.className = 'count-badge';
            countBadge.textContent = `×${count}`;
            countBadge.style.cssText = `
                position: absolute;
                top: -8px;
                right: -8px;
                background: #c8aa6e;
                color: #1e2328;
                border-radius: 50%;
                width: 20px;
                height: 20px;
                font-size: 12px;
                font-weight: bold;
                display: flex;
                align-items: center;
                justify-content: center;
                z-index: 10;
            `;

            const nodeWrapper = document.createElement('div');
            nodeWrapper.style.cssText = `
                position: relative;
                display: inline-block;
            `;

            nodeWrapper.appendChild(componentNode);
            nodeWrapper.appendChild(countBadge);
            duplicatesContainer.appendChild(nodeWrapper);
        } else {
            duplicatesContainer.appendChild(componentNode);
        }
    }

    branchContainer.appendChild(duplicatesContainer);

    if (component.components && component.components.length > 0) {
        const childrenRow = document.createElement('div');
        childrenRow.className = 'children-row';
        childrenRow.style.cssText = `
            display: flex;
            justify-content: center;
            gap: 20px;
            margin-top: 20px;
            position: relative;
        `;

        const childrenConnector = document.createElement('div');
        childrenConnector.className = 'children-connector';
        childrenConnector.style.cssText = `
            position: absolute;
            top: -20px;
            left: 50%;
            transform: translateX(-50%);
            width: 2px;
            height: 20px;
            background: #c8aa6e;
        `;

        childrenRow.appendChild(childrenConnector);

        component.components.forEach((childComponent) => {
            const childBranch = createComponentBranch(childComponent);
            childrenRow.appendChild(childBranch);
        });

        branchContainer.appendChild(childrenRow);
    }

    return branchContainer;
}

function createTreeNode(item, isRoot = false) {
    const node = document.createElement('div');
    node.className = `tree-node challenge-node ${isRoot ? 'root-node' : ''}`;
    node.setAttribute('data-item-id', item.id);
    node.style.cssText = `
        display: flex;
        flex-direction: column;
        align-items: center;
        position: relative;
    `;

    const questionMark = document.createElement('div');
    questionMark.className = 'question-mark';
    questionMark.innerHTML = '?';
    questionMark.style.cssText = `
        width: 50px;
        height: 50px;
        background: #1e2328;
        border: 2px solid #c8aa6e;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 24px;
        font-weight: bold;
        color: #c8aa6e;
    `;

    node.setAttribute('data-item-info', JSON.stringify({
        iconUrl: item.iconUrl || '',
        name: item.name || 'Unknown'
    }));

    node.appendChild(questionMark);

    return node;
}

function revealNode(itemId) {
    const nodes = document.querySelectorAll(`.tree-node[data-item-id="${itemId}"]`);
    nodes.forEach(node => {
        const questionMark = node.querySelector('.question-mark');

        if (questionMark && !node.classList.contains('revealed')) {
            const itemInfo = JSON.parse(node.getAttribute('data-item-info'));

            const icon = document.createElement('img');
            icon.className = 'node-icon';
            icon.src = itemInfo.iconUrl || '';
            icon.alt = itemInfo.name || '';
            icon.style.cssText = `
                width: 50px;
                height: 50px;
                border-radius: 8px;
                border: 2px solid #c8aa6e;
            `;

            questionMark.style.display = 'none';
            node.appendChild(icon);
            node.classList.add('revealed');

            icon.style.opacity = '0';
            icon.style.transition = 'opacity 0.3s ease';
            setTimeout(() => {
                icon.style.opacity = '1';
            }, 10);
        }
    });
}

function revealComponentTree(itemId) {
    const treeDataScript = document.getElementById('treeData');
    if (!treeDataScript) return;

    try {
        const treeDataText = treeDataScript.textContent || treeDataScript.innerText;
        const treeData = JSON.parse(treeDataText.trim());

        const findAndReveal = (component) => {
            if (component.item.id === itemId) {
                revealNode(itemId);
                if (component.components && component.components.length > 0) {
                    component.components.forEach(child => {
                        revealComponentTreeRecursive(child);
                    });
                }
                return true;
            }

            if (component.components) {
                for (let child of component.components) {
                    if (findAndReveal(child)) return true;
                }
            }
            return false;
        };

        findAndReveal(treeData);
    } catch (error) {
        console.error('Error revealing component tree:', error);
    }
}

function revealComponentTreeRecursive(component) {
    revealNode(component.item.id);
    if (component.components && component.components.length > 0) {
        component.components.forEach(child => {
            revealComponentTreeRecursive(child);
        });
    }
}

function revealAllTree() {
    const treeDataScript = document.getElementById('treeData');
    if (!treeDataScript) return;

    try {
        const treeDataText = treeDataScript.textContent || treeDataScript.innerText;
        const treeData = JSON.parse(treeDataText.trim());

        const revealAllRecursive = (component) => {
            revealNode(component.item.id);
            if (component.components && component.components.length > 0) {
                component.components.forEach(child => {
                    revealAllRecursive(child);
                });
            }
        };

        revealAllRecursive(treeData);
    } catch (error) {
        console.error('Error revealing all tree:', error);
    }
}

function selectItem(item) {
    if (guessedItems.has(item.id)) {
        showNotification('Вы уже выбирали этот предмет', 'error');
        return;
    }
    submitGuess(item);
}

function submitGuess(item) {
    const itemId = item.id;

    if (guessedItems.has(itemId)) {
        showNotification('Вы уже выбирали этот предмет', 'error');
        return;
    }

    console.log('Submitting guess for item:', item);

    fetch('/daily/guess/classic', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `itemId=${itemId}&guessType=component`
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok: ' + response.status);
            }
            return response.json();
        })
        .then(result => {
            console.log('Server response:', result);
            handleGuessResult(result, item);
        })
        .catch(error => {
            console.error('Error submitting guess:', error);
            showNotification('Ошибка сети: ' + error.message, 'error');
        });
}

function handleGuessResult(result, item) {
    const itemId = item.id;

    guessedItems.add(itemId);

    if (result.correct) {
        if (result.isRoot) {
            revealAllTree();
            addToGuessHistory(item, 'correct');
            showNotification(result.message || 'Поздравляем! Вы угадали корневой предмет!', 'success');
            document.getElementById('searchInput').disabled = true;
        } else {
            revealComponentTree(itemId);
            addToGuessHistory(item, 'component');
            showNotification(result.message || 'Правильно! Этот предмет есть в сборке', 'success');
        }
    } else {
        addToGuessHistory(item, 'wrong');
        showNotification(result.message || 'Этот предмет не входит в сборку', 'error');
    }

    document.getElementById('searchInput').value = '';
    document.getElementById('itemsList').innerHTML = '';
    renderComponentsGrid(allItems);
}

function addToGuessHistory(item, status) {
    guessHistory.push({
        item: item,
        status: status,
        timestamp: new Date()
    });
    renderGuessHistory();
}

function renderGuessHistory() {
    const historyContainer = document.getElementById('guessHistory');
    if (!historyContainer) return;

    historyContainer.innerHTML = '';

    guessHistory.forEach(guess => {
        const historyItem = document.createElement('div');
        historyItem.className = `history-item ${guess.status}`;

        historyItem.innerHTML = `
            <img src="${guess.item.iconUrl}" alt="${guess.item.name}" class="history-icon">
            <span class="history-name">${guess.item.name}</span>
            ${guess.status === 'correct' || guess.status === 'component' ?
            '<span class="history-arrow">→</span>' : ''}
        `;

        historyContainer.appendChild(historyItem);
    });
}

function searchItems() {
    const searchInput = document.getElementById('searchInput');
    if (!searchInput) return;

    const searchText = searchInput.value.toLowerCase();
    const filteredItems = allItems.filter(item =>
        item.name && item.name.toLowerCase().includes(searchText)
    ).slice(0, 10);

    renderComponentsGrid(filteredItems);
}

function renderComponentsGrid(items) {
    const grid = document.getElementById('itemsList');
    if (!grid) return;

    grid.innerHTML = '';

    items.forEach(item => {
        if (guessedItems.has(item.id)) return;

        const card = document.createElement('div');
        card.className = 'component-card';
        card.setAttribute('data-item-id', item.id);

        card.innerHTML = `
            <img src="${item.iconUrl || ''}" alt="${item.name || ''}" class="component-icon">
            <div class="component-name">${item.name || 'Unknown'}</div>
        `;

        card.addEventListener('click', () => selectItem(item));
        grid.appendChild(card);
    });
}

function safeParseInt(value) {
    if (value === null || value === undefined || value === '') {
        return 0;
    }
    const parsed = parseInt(value, 10);
    return isNaN(parsed) ? 0 : parsed;
}

function showNotification(message, type) {
    if (!message) return;

    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.textContent = message;
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 15px 20px;
        border-radius: 5px;
        color: white;
        z-index: 1000;
        font-weight: bold;
        ${type === 'success' ? 'background: #27ae60;' :
        type === 'error' ? 'background: #e74c3c;' : 'background: #f39c12;'}
    `;

    document.body.appendChild(notification);

    setTimeout(() => {
        if (notification.parentNode) {
            notification.remove();
        }
    }, 3000);
}