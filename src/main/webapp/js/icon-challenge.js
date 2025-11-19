let allItems = [];
let guessedItems = new Set();
let guessHistory = [];
let currentDifficulty = 3;

const difficultySettings = {
    1: {
        filter: 'none',
        transform: getRandomRotation(),
        label: 'Легко (25 очков)',
        score: 25
    },
    2: {
        filter: 'grayscale(100%)',
        transform: getRandomRotation(),
        label: 'Средне (50 очков)',
        score: 50
    },
    3: {
        filter: 'grayscale(100%) blur(4px)',
        transform: getRandomRotation(),
        label: 'Сложно (100 очков)',
        score: 100
    }
};

function getRandomRotation() {
    const rotations = ['rotate(0deg)', 'rotate(90deg)', 'rotate(180deg)', 'rotate(270deg)'];
    return rotations[Math.floor(Math.random() * rotations.length)];
}

function updateImageDifficulty(level) {
    const settings = difficultySettings[level];
    const image = document.getElementById('itemImage');
    const label = document.getElementById('difficultyLabel');

    if (image) {
        image.style.filter = settings.filter;
        image.style.transform = settings.transform;
    }
    if (label) {
        label.textContent = settings.label;
    }
    currentDifficulty = level;
}

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
    if (window.challengeData && window.challengeData.completed) {
        showNotification('Задание уже завершено', 'error');
        return;
    }

    if (guessedItems.has(item.id)) {
        showNotification('Вы уже выбирали этот предмет', 'error');
        return;
    }

    submitGuess(item);
}

function submitGuess(item) {
    const itemId = item.id;

    fetch('/daily/guess/icon', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `itemId=${itemId}&difficulty=${currentDifficulty}`
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
    const itemId = item.id;
    guessedItems.add(itemId);

    if (result.correct) {
        addToGuessHistory(item, 'correct');
        showNotification(result.message || `Поздравляем! Вы угадали предмет и заработали ${result.scoreEarned} очков!`, 'success');

        const searchInput = document.getElementById('searchInput');
        if (searchInput) {
            searchInput.disabled = true;
        }

        revealCorrectItem(item);

    } else {
        addToGuessHistory(item, 'wrong');
        showNotification(result.message || 'Это не тот предмет. Попробуйте еще раз!', 'error');
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
            statusText = '<span class="history-status">✓ Угадан</span>';
        } else {
            statusText = '<span class="history-status">✗ Не угадан</span>';
        }

        historyItem.innerHTML = `
            <img src="${guess.item.iconUrl}" alt="${guess.item.name}" class="history-icon">
            <span class="history-name">${guess.item.name}</span>
            ${statusText}
        `;

        historyContainer.appendChild(historyItem);
    });
}

function revealCorrectItem(item) {
    console.log('Правильный предмет:', item.name);

    const image = document.getElementById('itemImage');
    if (image) {
        image.style.filter = 'none';
        image.style.transform = 'none';
    }
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
    updateImageDifficulty(3);

    const difficultySlider = document.getElementById('difficulty');
    if (difficultySlider) {
        difficultySlider.value = 3;
        difficultySlider.addEventListener('input', function(e) {
            updateImageDifficulty(parseInt(e.target.value));
        });
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
            renderComponentsGrid(allItems);
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
        const image = document.getElementById('itemImage');
        if (image) {
            image.style.filter = 'none';
            image.style.transform = 'none';
        }
    }
}

function safeParseInt(value) {
    if (value === null || value === undefined || value === '') {
        return 0;
    }
    const parsed = parseInt(value, 10);
    return isNaN(parsed) ? 0 : parsed;
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

document.addEventListener('DOMContentLoaded', initializeChallenge);