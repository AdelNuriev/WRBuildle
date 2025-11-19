let allItems = [];
let guessedItems = new Set();
let guessHistory = [];
let attemptsCount = 0;
let targetItem = null;
let currentScore = window.gameData.currentScore || 0;
let currentStreak = window.gameData.currentStreak || 0;
let currentRound = window.gameData.currentRound || 1;

function searchItems() {
    const searchInput = document.getElementById('searchInput');
    if (!searchInput) return;

    const searchText = searchInput.value.toLowerCase();
    const filteredItems = allItems.filter(item =>
        item.name && item.name.toLowerCase().includes(searchText)
    );

    renderComponentsGrid(filteredItems);
}

function renderComponentsGrid(items) {
    const grid = document.getElementById('itemsList');
    if (!grid) return;

    grid.innerHTML = '';
    grid.classList.toggle('active', items.length > 0);

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

    const grid = document.getElementById('itemsList');
    if (grid) {
        grid.classList.remove('active');
    }

    submitGuess(item);
}

function submitGuess(item) {
    const itemId = item.id;

    fetch('/infinite/guess/infinite', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `itemId=${itemId}&targetItemId=${window.gameData.targetItemId}`
    })
        .then(response => {
            const contentType = response.headers.get('content-type');
            if (!contentType || !contentType.includes('application/json')) {
                return response.text().then(text => {
                    throw new Error('Server returned non-JSON response. Status: ' + response.status);
                });
            }
            return response.json();
        })
        .then(result => {
            handleGuessResult(result, item);
        })
        .catch(error => {
            console.error('Error submitting guess:', error);
            showNotification('Ошибка сети: ' + error.message, 'error');
        });
}

function handleGuessResult(result, item) {
    attemptsCount++;
    const itemId = item.id;
    guessedItems.add(itemId);

    addToGuessHistory(item, result);

    if (result.correct) {
        const score = calculateScore(attemptsCount);
        showNotification(`Поздравляем! ${result.message} Заработано очков: ${score}`, 'success');

        result.totalScore = currentScore + score;
        currentScore = result.totalScore;
        currentStreak = result.streak || currentStreak + 1;

        updateStats();
        revealTargetItem();

        setTimeout(() => {
            startNewRound(result.newTargetItemId);
        }, 1500);

    } else {
        currentStreak = result.streak || 0;
        currentScore = result.totalScore || currentScore;
        updateStats();
    }

    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.value = '';
    }
    renderComponentsGrid([]);
    renderGuessHistory();
}

function startNewRound(newTargetItemId) {
    attemptsCount = 0;
    guessedItems.clear();
    guessHistory = [];
    currentRound++;

    window.gameData.targetItemId = newTargetItemId;
    targetItem = allItems.find(item => item.id === newTargetItemId);

    document.getElementById('currentRound').textContent = currentRound;
    document.getElementById('attemptsCount').textContent = attemptsCount;

    resetProperties();
    clearGuessHistory();

    showNotification(`Начался раунд ${currentRound}! Угадайте следующий предмет`, 'info');
}

function resetProperties() {
    const rarityProperty = document.getElementById('rarityProperty');
    const effectTypeProperty = document.getElementById('effectTypeProperty');
    const costProperty = document.getElementById('costProperty');

    if (rarityProperty) {
        rarityProperty.textContent = '???';
        rarityProperty.className = 'property-value unknown';
    }

    if (effectTypeProperty) {
        effectTypeProperty.textContent = '???';
        effectTypeProperty.className = 'property-value unknown';
    }

    if (costProperty) {
        costProperty.textContent = '???';
        costProperty.className = 'property-value unknown';
    }
}

function clearGuessHistory() {
    const historyContainer = document.getElementById('guessHistory');
    if (historyContainer) {
        historyContainer.innerHTML = '';
    }
}

function calculateScore(attempts) {
    const baseScore = 150;
    const funcParameter = 0.1555*attempts
    const penalty = Math.pow(Math.E, -1 * funcParameter);
    const score = baseScore - penalty;
    return Math.max(0, Math.floor(score));
}

function addToGuessHistory(item, result) {
    guessHistory.push({
        item: item,
        result: result,
        timestamp: new Date(),
        attemptNumber: attemptsCount
    });
}

function renderGuessHistory() {
    const historyContainer = document.getElementById('guessHistory');
    if (!historyContainer) return;

    historyContainer.innerHTML = '';

    guessHistory.forEach((guess, index) => {
        const guessRow = document.createElement('div');
        guessRow.className = 'guess-row';

        const attributesComparison = compareAttributes(guess.item, targetItem);
        const rarityClass = guess.item.rarity === targetItem.rarity ? 'correct' : 'incorrect';
        const effectTypeClass = guess.item.effectType === targetItem.effectType ? 'correct' : 'incorrect';
        const costComparison = compareCost(guess.item.cost, targetItem.cost);

        guessRow.innerHTML = `
            <div class="guess-cell">
                <img src="${guess.item.iconUrl}" alt="${guess.item.name}" class="guess-icon">
            </div>
            <div class="guess-cell">
                <div class="attributes-container">
                    ${attributesComparison.length > 0 ? attributesComparison.map(attr => `
                        <span class="attribute-tag ${attr.status}">${attr.displayName}</span>
                    `).join('') : '<span class="attribute-tag incorrect">Нет свойств</span>'}
                </div>
            </div>
            <div class="guess-cell">
                <span class="effect-type-indicator ${effectTypeClass}">${guess.item.effectType || 'None'}</span>
            </div>
            <div class="guess-cell">
                <span class="rarity-indicator ${rarityClass}">${guess.item.rarity || 'Common'}</span>
            </div>
            <div class="guess-cell">
                <div class="cost-indicator">
                    <span class="cost-value ${costComparison.class}">${guess.item.cost || 0}g</span>
                    ${costComparison.arrow ? `<span class="cost-arrow ${costComparison.arrow}">${costComparison.arrow === 'up' ? '↑' : '↓'}</span>` : ''}
                </div>
            </div>
        `;

        historyContainer.appendChild(guessRow);
    });

    updateAttemptsCounter();
}

function compareAttributes(guessedItem, targetItem) {
    if (!guessedItem.attributes || !targetItem.attributes ||
        !Array.isArray(guessedItem.attributes) || !Array.isArray(targetItem.attributes)) {
        return [];
    }

    const getAttributeDisplayName = (attr) => {
        if (typeof attr === 'string') return attr;
        return attr.displayName || attr.name || 'Unknown';
    };

    const getAttributeId = (attr) => {
        if (typeof attr === 'string') return attr;
        return attr.name || attr.toString();
    };

    const guessedAttrs = guessedItem.attributes.map(attr => ({
        id: getAttributeId(attr),
        displayName: getAttributeDisplayName(attr)
    }));

    const targetAttrs = targetItem.attributes.map(attr => ({
        id: getAttributeId(attr),
        displayName: getAttributeDisplayName(attr)
    }));

    const allMatch = guessedAttrs.length === targetAttrs.length &&
        guessedAttrs.every(gAttr =>
            targetAttrs.some(tAttr => tAttr.id === gAttr.id)
        );

    return guessedAttrs.map(guessedAttr => {
        const isInTarget = targetAttrs.some(targetAttr => targetAttr.id === guessedAttr.id);

        let status = 'incorrect';
        if (allMatch) {
            status = 'correct';
        } else if (isInTarget) {
            status = 'partial';
        }

        return {
            displayName: guessedAttr.displayName,
            status: status
        };
    });
}

function compareCost(guessedCost, targetCost) {
    guessedCost = parseInt(guessedCost) || 0;
    targetCost = parseInt(targetCost) || 0;

    if (guessedCost === targetCost) {
        return { class: 'correct', arrow: null };
    } else if (guessedCost > targetCost) {
        return { class: 'incorrect', arrow: 'down' };
    } else {
        return { class: 'incorrect', arrow: 'up' };
    }
}

function updateAttemptsCounter() {
    const attemptsCounter = document.getElementById('attemptsCount');
    if (attemptsCounter) {
        attemptsCounter.textContent = attemptsCount;
    }
}

function updateStats() {
    const scoreElement = document.getElementById('currentScore');
    const streakElement = document.getElementById('currentStreak');

    if (scoreElement) scoreElement.textContent = currentScore;
    if (streakElement) streakElement.textContent = currentStreak;
}

function revealTargetItem() {
    if (!targetItem) return;

    const rarityProperty = document.getElementById('rarityProperty');
    const effectTypeProperty = document.getElementById('effectTypeProperty');
    const costProperty = document.getElementById('costProperty');

    if (rarityProperty) {
        rarityProperty.textContent = targetItem.rarity || 'Common';
        rarityProperty.className = 'property-value correct';
    }

    if (effectTypeProperty) {
        effectTypeProperty.textContent = targetItem.effectType || 'None';
        effectTypeProperty.className = 'property-value correct';
    }

    if (costProperty) {
        costProperty.textContent = `${targetItem.cost || 0}g`;
        costProperty.className = 'property-value correct';
    }
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
        type === 'error' ? 'background: #e74c3c;' :
            type === 'info' ? 'background: #3498db;' : 'background: #f39c12;'}
    `;

    document.body.appendChild(notification);

    setTimeout(() => {
        if (notification.parentNode) {
            notification.remove();
        }
    }, 3000);
}

function initializeGame() {
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('input', searchItems);

        document.addEventListener('click', (e) => {
            const grid = document.getElementById('itemsList');
            if (grid && !grid.contains(e.target) && e.target !== searchInput) {
                grid.classList.remove('active');
            }
        });
    }

    fetch('/daily/api/items')
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok: ' + response.status);
            }
            return response.json();
        })
        .then(items => {
            allItems = items.map(item => ({
                ...item,
                id: safeParseInt(item.id),
                cost: item.cost || 0,
                rarity: item.rarity || 'Common',
                effectType: item.effectType || 'Passive',
                attributes: item.attributes || []
            }));

            const targetItemId = window.gameData?.targetItemId;
            console.log(targetItemId);
            if (targetItemId) {
                targetItem = allItems.find(item => item.id === targetItemId);
            }


            renderComponentsGrid([]);
        })
        .catch(error => {
            console.error('Error loading items:', error);
            showNotification('Ошибка загрузки ов: ' + error.message, 'error');
        });

    updateStats();
    updateAttemptsCounter();
}

function safeParseInt(value) {
    if (value === null || value === undefined || value === '') {
        return 0;
    }
    const parsed = parseInt(value, 10);
    return isNaN(parsed) ? 0 : parsed;
}

window.searchItems = searchItems;
window.selectItem = selectItem;

document.addEventListener('DOMContentLoaded', initializeGame);