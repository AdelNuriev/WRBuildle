let allItems = [];
let guessedItems = new Set();
let guessHistory = [];
let imposterItemId = null;
let targetItemId = null;
let imposterItem = null;
let originalTreeData = null;
let replacedItemInfo = null;

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

    console.log('Submitting imposter guess:', item);

    fetch('/daily/guess/imposter', {
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
        showNotification(result.message || `Поздравляем! Вы нашли импостера! +${result.scoreEarned} очков`, 'success');

        const searchInput = document.getElementById('searchInput');
        if (searchInput) {
            searchInput.disabled = true;
        }

        restoreOriginalItem();

    } else {
        addToGuessHistory(item, 'wrong');
        showNotification(result.message || 'Это не импостер', 'error');
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
            statusText = '<span class="history-status">Импостер</span>';
        } else {
            statusText = '<span class="history-status">Не импостер</span>';
        }

        historyItem.innerHTML = `
            <img src="${guess.item.iconUrl}" alt="${guess.item.name}" class="history-icon">
            <span class="history-name">${guess.item.name}</span>
            ${statusText}
        `;

        historyContainer.appendChild(historyItem);
    });
}

function restoreOriginalItem() {
    if (!replacedItemInfo) return;

    const imposterNodes = document.querySelectorAll(`.tree-node[data-item-id="${imposterItemId}"][data-node-id="${replacedItemInfo.nodeId}"]`);
    imposterNodes.forEach(node => {
        const icon = node.querySelector('.node-icon');
        if (icon) {
            icon.src = replacedItemInfo.originalItem.iconUrl || '';
            icon.alt = replacedItemInfo.originalItem.name || '';
        }

        const marker = node.querySelector('.imposter-marker');
        if (marker) {
            marker.style.display = 'flex';
            marker.innerHTML = '👻';
            marker.title = 'Здесь был импостер';
        }

        node.classList.add('restored', 'imposter-highlighted');
    });
}

function renderTreeWithImposter(treeData) {
    const treeContainer = document.getElementById('treeContainer');
    if (!treeContainer) return;

    treeContainer.innerHTML = '';

    if (!treeData || typeof treeData !== 'object') {
        renderFallbackTree();
        return;
    }

    originalTreeData = JSON.parse(JSON.stringify(treeData));

    const modifiedTreeData = replaceRandomItemWithImposter(treeData);

    renderFullImposterTree(modifiedTreeData);
}

function replaceRandomItemWithImposter(treeData) {
    if (!imposterItem) {
        console.error('Imposter item is not defined!');
        return treeData;
    }

    console.log('Replacing random item with imposter:', imposterItem);

    const allNodes = [];
    collectAllNodes(treeData, allNodes);

    if (allNodes.length === 0) {
        console.error('No nodes found in tree data');
        return treeData;
    }

    let randomNode;
    if (allNodes.length === 1) {
        randomNode = allNodes[0];
    } else {
        const nodesWithoutRoot = allNodes.filter(node => node !== treeData);
        randomNode = nodesWithoutRoot[Math.floor(Math.random() * nodesWithoutRoot.length)] || allNodes[0];
    }

    replacedItemInfo = {
        nodeId: generateNodeId(),
        originalItem: { ...randomNode.item },
        position: getNodePosition(randomNode, treeData)
    };

    randomNode.item = {
        id: imposterItemId,
        name: imposterItem.name,
        iconUrl: imposterItem.iconUrl,
        cost: imposterItem.cost
    };

    console.log('Replaced item:', replacedItemInfo.originalItem.name, 'with imposter at position:', replacedItemInfo.position);

    return treeData;
}

function collectAllNodes(node, nodesList) {
    if (node && node.item) {
        nodesList.push(node);
    }

    if (node.components) {
        node.components.forEach(component => {
            collectAllNodes(component, nodesList);
        });
    }
}

function getNodePosition(node, treeData) {
    const path = [];

    function findPath(currentNode, targetNode, currentPath) {
        if (currentNode === targetNode) {
            path.push(...currentPath);
            return true;
        }

        if (currentNode.components) {
            for (let i = 0; i < currentNode.components.length; i++) {
                if (findPath(currentNode.components[i], targetNode, [...currentPath, `components[${i}]`])) {
                    return true;
                }
            }
        }

        return false;
    }

    findPath(treeData, node, ['root']);
    return path.join('.');
}

function generateNodeId() {
    return 'node_' + Math.random().toString(36).substr(2, 9);
}

function renderFullImposterTree(treeData) {
    const treeContainer = document.getElementById('treeContainer');
    if (!treeContainer) return;

    const isRootImposter = treeData.item && treeData.item.id === imposterItemId;
    const rootNode = createTreeNode(treeData.item, true, isRootImposter, replacedItemInfo?.nodeId);
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

function createTreeNode(item, isRoot, isImposter, nodeId = null) {
    const node = document.createElement('div');
    node.className = `tree-node challenge-node ${isRoot ? 'root-node' : ''} ${isImposter ? 'imposter-node' : ''}`;
    node.setAttribute('data-item-id', item.id);
    if (nodeId) {
        node.setAttribute('data-node-id', nodeId);
    }

    const icon = document.createElement('img');
    icon.className = 'node-icon';

    icon.src = item.iconUrl || '';
    icon.alt = item.name || '';

    icon.style.cssText = `
        width: 50px;
        height: 50px;
        border-radius: 8px;
        background: #1e2328;
    `;

    node.appendChild(icon);
    node.style.cssText = `
        display: flex;
        flex-direction: column;
        align-items: center;
        padding: 10px;
        margin: 5px;
        position: relative;
    `;

    if (isImposter) {
        const imposterMarker = document.createElement('div');
        imposterMarker.className = 'imposter-marker';
        imposterMarker.innerHTML = '?';
        node.appendChild(imposterMarker);
    }

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
    const isImposter = component.item.id === imposterItemId;
    const nodeId = isImposter ? replacedItemInfo?.nodeId : null;

    for (let i = 0; i < count; i++) {
        const componentNode = createTreeNode(component.item, false, isImposter, nodeId);

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
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 15px 20px;
        border-radius: 5px;
        color: white;
        font-weight: bold;
        z-index: 1000;
        max-width: 300px;
    `;

    if (type === 'success') {
        notification.style.background = '#27ae60';
    } else if (type === 'error') {
        notification.style.background = '#e74c3c';
    } else {
        notification.style.background = '#3498db';
    }

    document.body.appendChild(notification);

    setTimeout(() => {
        if (notification.parentNode) {
            notification.remove();
        }
    }, 3000);
}

function initializeChallenge() {
    console.log('Initializing imposter challenge...');

    imposterItemId = window.challengeData?.imposterItemId;
    targetItemId = window.challengeData?.targetItemId;

    console.log('Imposter ID:', imposterItemId);
    console.log('Target ID:', targetItemId);

    if (!imposterItemId) {
        console.error('Imposter item ID is not defined!');
        showNotification('Ошибка: ID импостера не определен', 'error');
        return;
    }

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

            imposterItem = allItems.find(item => item.id === imposterItemId);
            console.log('Found imposter item:', imposterItem);

            if (!imposterItem) {
                console.error('Imposter item not found in items list');
                showNotification('Ошибка: предмет импостера не найден', 'error');
                return;
            }

            renderItemsGrid(allItems);

            initializeImposterTree();
        })
        .catch(error => {
            console.error('Error loading items:', error);
            showNotification('Ошибка загрузки предметов: ' + error.message, 'error');
        });

    if (window.challengeData && window.challengeData.completed) {
        console.log('Challenge already completed');
        const searchInput = document.getElementById('searchInput');
        if (searchInput) {
            searchInput.disabled = true;
        }
    }
}

function initializeImposterTree() {
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
        console.log('Successfully parsed tree data:', treeData);

        renderTreeWithImposter(treeData);
    } catch (error) {
        console.error('Error parsing tree data:', error);
        console.error('Problematic JSON:', treeDataText.substring(0, 200));
        renderFallbackTree();
    }
}

function safeParseInt(value) {
    if (value === null || value === undefined || value === '') {
        return 0;
    }
    const parsed = parseInt(value, 10);
    return isNaN(parsed) ? 0 : parsed;
}

document.addEventListener('DOMContentLoaded', initializeChallenge);