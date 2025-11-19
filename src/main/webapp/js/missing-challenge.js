let allItems = [];
let guessedItems = new Set();
let guessHistory = [];
let missingItemId = null;
let hiddenDuplicateIndex = null;

function searchItems() {
    const searchInput = document.getElementById('searchInput');
    if (!searchInput) return;

    const searchText = searchInput.value.toLowerCase();
    const filteredItems = allItems.filter(item =>
        item.name && item.name.toLowerCase().includes(searchText)
    ).slice(0, 10);

    renderItemsGrid(filteredItems);
}

function renderItemsGrid(items) {
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

function selectItem(item) {
    if (guessedItems.has(item.id)) {
        showNotification('Вы уже выбирали этот предмет', 'error');
        return;
    }

    if (window.challengeData && window.challengeData.completed) {
        showNotification('Задание уже завершено', 'error');
        return;
    }

    submitGuess(item);
}

function submitGuess(item) {
    const itemId = item.id;

    console.log('Submitting missing item guess:', item);

    fetch('/daily/guess/missing', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `itemId=${itemId}`
    })
        .then(response => {
            console.log('Response status:', response.status, response.statusText);

            if (!response.ok) {
                return response.text().then(errorText => {
                    throw new Error(`HTTP ${response.status}: ${errorText}`);
                });
            }
            return response.json();
        })
        .then(result => {
            console.log('Server response:', result);
            handleGuessResult(result, item);
        })
        .catch(error => {
            console.error('Error submitting guess:', error);
            showNotification('Ошибка отправки предположения: ' + error.message, 'error');
        });
}

function handleGuessResult(result, item) {
    const itemId = item.id;
    guessedItems.add(itemId);

    if (result.correct) {
        addToGuessHistory(item, 'correct');
        showNotification(result.message || `Поздравляем! Вы нашли пропущенный предмет! +${result.scoreEarned} очков`, 'success');

        const searchInput = document.getElementById('searchInput');
        if (searchInput) {
            searchInput.disabled = true;
        }

        revealMissingItem(item);

    } else {
        addToGuessHistory(item, 'wrong');
        showNotification(result.message || 'Это не пропущенный предмет', 'error');
    }

    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.value = '';
    }
    renderItemsGrid(allItems);
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

    const sortedHistory = [...guessHistory].reverse();

    sortedHistory.forEach(guess => {
        const historyItem = document.createElement('div');
        historyItem.className = `history-item ${guess.status}`;

        let statusText = '';
        if (guess.status === 'correct') {
            statusText = '<span class="history-status">Найден</span>';
        } else {
            statusText = '<span class="history-status">Не найден</span>';
        }

        historyItem.innerHTML = `
            <img src="${guess.item.iconUrl}" alt="${guess.item.name}" class="history-icon">
            <span class="history-name">${guess.item.name}</span>
            ${statusText}
        `;

        historyContainer.appendChild(historyItem);
    });
}

function revealMissingItem(item) {
    revealNode(missingItemId, item, hiddenDuplicateIndex);
}

function revealNode(itemId, item, duplicateIndex = null) {
    const nodes = document.querySelectorAll(`.tree-node[data-item-id="${itemId}"]`);

    nodes.forEach((node, index) => {
        if (duplicateIndex !== null && index !== duplicateIndex) {
            return;
        }

        const questionMark = node.querySelector('.question-mark');

        if (questionMark && !node.classList.contains('revealed')) {
            const icon = document.createElement('img');
            icon.className = 'node-icon';
            icon.src = item.iconUrl || '';
            icon.alt = item.name || '';
            icon.style.cssText = `
                width: 50px;
                height: 50px;
                border-radius: 8px;
                border: 2px solid #27ae60;
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

function renderTreeWithMissing(treeData) {
    const treeContainer = document.getElementById('treeContainer');
    if (!treeContainer) return;

    treeContainer.innerHTML = '';

    if (!treeData || typeof treeData !== 'object') {
        renderFallbackTree();
        return;
    }

    renderFullMissingTree(treeData);
}

function renderFullMissingTree(treeData) {
    const treeContainer = document.getElementById('treeContainer');
    if (!treeContainer) return;

    const rootNode = createTreeNode(treeData.item, true, treeData.item.id === missingItemId);
    treeContainer.appendChild(rootNode);

    if (treeData.components && treeData.components.length > 0) {
        const verticalConnector = document.createElement('div');
        verticalConnector.className = 'vertical-connector';
        treeContainer.appendChild(verticalConnector);
    }

    if (treeData.components && treeData.components.length > 0) {
        const componentsRow = document.createElement('div');
        componentsRow.className = 'components-row';

        const horizontalConnector = document.createElement('div');
        horizontalConnector.className = 'horizontal-connector';
        componentsRow.appendChild(horizontalConnector);

        treeData.components.forEach((component, index) => {
            const componentBranch = createComponentBranch(component, index, treeData.components.length);
            componentsRow.appendChild(componentBranch);
        });

        treeContainer.appendChild(componentsRow);
    }
}

function createTreeNode(item, isRoot, isMissing) {
    const node = document.createElement('div');
    node.className = `tree-node challenge-node ${isRoot ? 'root-node' : ''} ${isMissing ? 'missing-node' : ''}`;
    node.setAttribute('data-item-id', item.id);

    node.setAttribute('data-item-info', JSON.stringify({
        iconUrl: item.iconUrl || '',
        name: item.name || 'Unknown'
    }));

    if (isMissing) {
        console.log('Creating missing node for item:', item.name, 'ID:', item.id);
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
        node.appendChild(questionMark);
    } else {
        const icon = document.createElement('img');
        icon.className = 'node-icon';
        icon.src = item.iconUrl || '';
        icon.alt = item.name || '';
        icon.style.cssText = `
            width: 50px;
            height: 50px;
            border-radius: 8px;
            border: 2px solid #c8aa6e;
        `;
        node.appendChild(icon);
    }

    node.style.cssText = `
        display: flex;
        flex-direction: column;
        align-items: center;
        padding: 10px;
        margin: 5px;
    `;

    return node;
}

function createComponentBranch(component, componentIndex, totalComponents) {
    const branchContainer = document.createElement('div');
    branchContainer.className = 'component-branch';
    branchContainer.style.cssText = `
        display: flex;
        flex-direction: column;
        align-items: center;
        position: relative;
    `;

    const branchConnector = document.createElement('div');
    branchConnector.className = 'branch-connector';
    branchConnector.style.cssText = `
        width: 2px;
        height: 20px;
        background: #c8aa6e;
        margin-bottom: 10px;
    `;
    branchContainer.appendChild(branchConnector);

    const count = component.quantity || component.count || 1;
    const isMissing = component.item.id === missingItemId;

    console.log('Component:', component.item.name, 'isMissing:', isMissing, 'count:', count);

    let shouldHide = false;
    if (isMissing && !hiddenDuplicateIndex) {
        shouldHide = true;
        hiddenDuplicateIndex = true;
        console.log('Hiding first occurrence of missing item:', component.item.name);
    }

    for (let i = 0; i < count; i++) {
        const componentNode = createTreeNode(component.item, false, shouldHide);

        if (count > 1) {
            const countBadge = document.createElement('div');
            countBadge.className = 'quantity-badge';
            countBadge.textContent = `×${count}`;
            countBadge.style.cssText = `
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
                z-index: 10;
            `;

            const nodeWrapper = document.createElement('div');
            nodeWrapper.style.cssText = `
                position: relative;
                display: inline-block;
                margin: 5px;
            `;
            nodeWrapper.appendChild(componentNode);
            nodeWrapper.appendChild(countBadge);
            branchContainer.appendChild(nodeWrapper);
        } else {
            branchContainer.appendChild(componentNode);
        }

        if (shouldHide) {
            shouldHide = false;
        }
    }

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

        component.components.forEach((childComponent, childIndex) => {
            const childBranch = createComponentBranch(childComponent, childIndex, component.components.length);
            childrenRow.appendChild(childBranch);
        });

        branchContainer.appendChild(childrenRow);
    }

    return branchContainer;
}

function renderFallbackTree() {
    const treeContainer = document.getElementById('treeContainer');
    if (!treeContainer) return;

    treeContainer.innerHTML = `
        <div style="text-align: center; color: #a09b8c; padding: 40px;">
            <div>⚠️ Дерево сборки недоступно</div>
        </div>
    `;
}

function showNotification(message, type) {
    if (!message) return;

    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.textContent = message;

    document.body.appendChild(notification);

    setTimeout(() => {
        if (notification.parentNode) {
            notification.remove();
        }
    }, 3000);
}

function initializeChallenge() {
    console.log('Initializing missing item challenge...');

    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('input', searchItems);
    }

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

    initializeMissingTree();

    if (window.challengeData && window.challengeData.completed) {
        console.log('Challenge already completed');
        const searchInput = document.getElementById('searchInput');
        if (searchInput) {
            searchInput.disabled = true;
        }
    }
}

function initializeMissingTree() {
    const treeDataScript = document.getElementById('treeData');
    if (!treeDataScript) {
        console.error('Tree data script element not found');
        renderFallbackTree();
        return;
    }

    const treeDataText = treeDataScript.textContent || treeDataScript.innerText;

    if (!treeDataText || treeDataText.trim() === '') {
        console.error('Tree data text is empty');
        renderFallbackTree();
        return;
    }

    try {
        const cleanedText = treeDataText.trim();
        const treeData = JSON.parse(cleanedText);
        console.log('Successfully parsed missing tree data:', treeData);

        missingItemId = window.challengeData.missingItemId;
        hiddenDuplicateIndex = null;
        renderTreeWithMissing(treeData);
    } catch (error) {
        console.error('Error parsing tree data:', error);
        console.error('Problematic JSON:', treeDataText.substring(0, 200));
        renderFallbackTree();
    }
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

document.addEventListener('DOMContentLoaded', initializeChallenge);