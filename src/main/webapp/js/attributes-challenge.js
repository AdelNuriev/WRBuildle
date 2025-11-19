let allItems = [];
let guessedItems = new Set();
let guessHistory = [];
let attemptsCount = 0;
let targetItem = null;

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
    if (window.challengeData && window.challengeData.completed) {
        showNotification('Задание уже завершено', 'error');
        return;
    }

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

    fetch('/daily/guess/attributes', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `itemId=${itemId}`
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok: ' + response.status);
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
        showNotification(`Поздравляем! Вы угадали предмет и заработали ${score} очков!`, 'success');

        const searchInput = document.getElementById('searchInput');
        if (searchInput) {
            searchInput.disabled = true;
        }

        revealTargetItem();
    } else {
        showNotification('Это не тот предмет. Попробуйте еще раз!', 'error');
    }

    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.value = '';
    }
    renderComponentsGrid([]);
    renderGuessHistory();
}

function calculateScore(attempts) {
    const baseScore = 150;
    const penalty = Math.pow(1/Math.E, attempts);
    const score = baseScore * (1 - penalty);
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
        if (typeof attr === 'string') {
            try {
                const enumValue = Object.values(ItemAttributes).find(
                    itemAttr => itemAttr.name() === attr || itemAttr.displayName === attr
                );
                return enumValue ? enumValue.displayName : attr;
            } catch {
                return attr;
            }
        }

        if (attr.displayName) {
            return attr.displayName;
        }

        if (attr.name) {
            try {
                const enumValue = Object.values(ItemAttributes).find(
                    itemAttr => itemAttr.name() === attr.name
                );
                return enumValue ? enumValue.displayName : attr.name;
            } catch {
                return attr.name || 'Unknown';
            }
        }

        return 'Unknown';
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
        return { class: 'incorrect', arrow: 'up' };
    } else {
        return { class: 'incorrect', arrow: 'down' };
    }
}

function updateAttemptsCounter() {
    const attemptsCounter = document.getElementById('attemptsCounter');
    if (attemptsCounter) {
        attemptsCounter.textContent = `${attemptsCount} ${getAttemptsWord(attemptsCount)}`;
    }
}

function getAttemptsWord(count) {
    if (count % 10 === 1 && count % 100 !== 11) return 'попытка';
    if ([2, 3, 4].includes(count % 10) && ![12, 13, 14].includes(count % 100)) return 'попытки';
    return 'попыток';
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
        type === 'error' ? 'background: #e74c3c;' : 'background: #f39c12;'}
    `;

    document.body.appendChild(notification);

    setTimeout(() => {
        if (notification.parentNode) {
            notification.remove();
        }
    }, 3000);
}

const ItemAttributes = {
    ATTACK_DAMAGE: { name: 'ATTACK_DAMAGE', displayName: 'AD' },
    CRITICAL_STRIKE_CHANCE: { name: 'CRITICAL_STRIKE_CHANCE', displayName: 'Crit' },
    ATTACK_SPEED: { name: 'ATTACK_SPEED', displayName: 'AS' },
    ON_HIT_EFFECTS: { name: 'ON_HIT_EFFECTS', displayName: 'On-hit' },
    ARMOR_PENETRATION: { name: 'ARMOR_PENETRATION', displayName: 'Lethality' },
    ABILITY_POWER: { name: 'ABILITY_POWER', displayName: 'AP' },
    MANA: { name: 'MANA', displayName: 'Mana' },
    MANA_RESTORATION: { name: 'MANA_RESTORATION', displayName: 'Mana rest' },
    MAGICAL_PENETRATION: { name: 'MAGICAL_PENETRATION', displayName: 'MP' },
    HEALTH: { name: 'HEALTH', displayName: 'HP' },
    HEALTH_RESTORATION: { name: 'HEALTH_RESTORATION', displayName: 'HP rest' },
    ARMOR: { name: 'ARMOR', displayName: 'Armor' },
    MAGIC_RESISTANCE: { name: 'MAGIC_RESISTANCE', displayName: 'MR' },
    ABILITY_HASTE: { name: 'ABILITY_HASTE', displayName: 'AH' },
    MOVE_SPEED: { name: 'MOVE_SPEED', displayName: 'MS' },
    PHYSICAL_VAMPIRISM: { name: 'PHYSICAL_VAMPIRISM', displayName: 'Vamp' },
    OMNIVAMP: { name: 'OMNIVAMP', displayName: 'Omni' }
};

function initializeChallenge() {
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
            console.log('Loaded items:', items);
            allItems = items.map(item => ({
                ...item,
                id: safeParseInt(item.id),
                cost: item.cost || 0,
                rarity: item.rarity || 'Common',
                effectType: item.effectType || 'Passive',
                attributes: item.attributes || []
            }));

            const targetItemId = window.challengeData?.targetItemId;
            if (targetItemId) {
                targetItem = allItems.find(item => item.id === targetItemId);
            }

            if (window.challengeData && window.challengeData.completed && targetItem) {
                revealTargetItem();
            }

            renderComponentsGrid([]);
        })
        .catch(error => {
            console.error('Error loading items:', error);
            showNotification('Ошибка загрузки предметов: ' + error.message, 'error');
        });

    if (window.challengeData && window.challengeData.completed) {
        const searchInput = document.getElementById('searchInput');
        if (searchInput) {
            searchInput.disabled = true;
        }
    }

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

document.addEventListener('DOMContentLoaded', initializeChallenge);