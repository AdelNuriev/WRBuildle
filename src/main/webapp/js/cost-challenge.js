let guessHistory = [];
let currentCost = 0;

function submitGuess() {
    const costInput = document.getElementById('costInput');
    const guessedCost = parseInt(costInput.value);

    if (isNaN(guessedCost) || guessedCost < 0 || guessedCost > 5000) {
        showNotification('Пожалуйста, введите корректную стоимость (0-5000)', 'error');
        return;
    }

    if (window.challengeData && window.challengeData.completed) {
        showNotification('Задание уже завершено', 'error');
        return;
    }

    console.log('Submitting cost guess:', guessedCost);

    fetch('/daily/guess/cost', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `guessedCost=${guessedCost}`
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
            handleGuessResult(result, guessedCost);
        })
        .catch(error => {
            console.error('Error submitting guess:', error);
            showNotification('Ошибка отправки предположения: ' + error.message, 'error');
        });
}

function handleGuessResult(result, guessedCost) {
    console.log('Handling guess result:', result);

    const correct = result.correct || false;
    const message = result.message || '';
    const scoreEarned = result.score || 0;

    if (correct) {
        addToGuessHistory(guessedCost, 'correct');
        showNotification('Поздравляем! Вы угадали стоимость!' + (scoreEarned ? ` +${scoreEarned} очков` : ''), 'success');

        const costInput = document.getElementById('costInput');
        if (costInput) {
            costInput.disabled = true;
        }
        const button = document.querySelector('.btn-primary');
        if (button) {
            button.disabled = true;
        }

    } else {
        let status = 'too-low';
        let displayMessage = 'Слишком мало';

        if (message === 'Higher' || message === 'Lower') {
            status = message === 'Higher' ? 'too-low' : 'too-high';
            displayMessage = message === 'Higher' ? 'Слишком мало' : 'Слишком много';
        } else if (message.includes('more') || message.includes('higher')) {
            status = 'too-low';
            displayMessage = 'Слишком мало';
        } else if (message.includes('less') || message.includes('lower')) {
            status = 'too-high';
            displayMessage = 'Слишком много';
        } else if (message === 'Already completed') {
            displayMessage = 'Задание уже завершено';
        } else {
            displayMessage = message;
        }

        addToGuessHistory(guessedCost, status);
        showNotification(displayMessage, 'error');
    }

    const costInput = document.getElementById('costInput');
    if (costInput) {
        costInput.value = '';
    }
}

function addToGuessHistory(guessedCost, status) {
    guessHistory.push({
        guessedCost: guessedCost,
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
            statusText = `<span class="history-status">Правильно!</span>`;
        } else if (guess.status === 'too-low') {
            statusText = `<span class="history-status">Слишком мало</span>`;
        } else if (guess.status === 'too-high') {
            statusText = `<span class="history-status">Слишком много</span>`;
        }

        historyItem.innerHTML = `
            <div class="history-cost">${guess.guessedCost} золота</div>
            ${statusText}
        `;

        historyContainer.appendChild(historyItem);
    });
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
    console.log('Initializing cost challenge...');

    const costInput = document.getElementById('costInput');
    if (costInput) {
        costInput.addEventListener('input', function(e) {
            const value = parseInt(e.target.value) || 0;
            currentCost = value;
        });

        costInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                submitGuess();
            }
        });
    }

    initializeItemTree();

    if (window.challengeData && window.challengeData.completed) {
        console.log('Challenge already completed');
        const costInput = document.getElementById('costInput');
        if (costInput) {
            costInput.disabled = true;
        }
        const button = document.querySelector('.btn-primary');
        if (button) {
            button.disabled = true;
        }
    }
}

function initializeItemTree() {
    const treeDataScript = document.getElementById('treeData');
    if (!treeDataScript) {
        console.error('Tree data script element not found');
        return;
    }

    const treeDataText = treeDataScript.textContent || treeDataScript.innerText;

    if (!treeDataText || treeDataText.trim() === '') {
        console.error('Tree data text is empty');
        return;
    }

    try {
        const cleanedText = treeDataText.trim();
        const treeData = JSON.parse(cleanedText);
        console.log('Successfully parsed tree data:', treeData);
        renderItemTree(treeData);
    } catch (error) {
        console.error('Error parsing tree data:', error);
        renderFallbackTree();
    }
}

function renderItemTree(treeData) {
    const treeContainer = document.getElementById('treeContainer');
    if (!treeContainer) return;

    treeContainer.innerHTML = '';

    if (!treeData || typeof treeData !== 'object') {
        renderFallbackTree();
        return;
    }

    if (typeof renderChallengeTree === 'function') {
        renderChallengeTree(treeData);
    } else {
        const rootNode = document.createElement('div');
        rootNode.className = 'tree-node root-node revealed';
        rootNode.innerHTML = `
            <img src="${treeData.item.iconUrl}" alt="${treeData.item.name}" class="node-icon">
            <div class="node-name">${treeData.item.name}</div>
        `;
        treeContainer.appendChild(rootNode);
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
        <div style="text-align: center; color: #a09b8c;">
            Дерево сборки недоступно
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
    questionMark.innerHTML = `<img src=${item.iconUrl} style="width: 40px; height: 40px;">`
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

document.addEventListener('DOMContentLoaded', initializeChallenge);