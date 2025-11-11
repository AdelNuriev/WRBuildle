let allItems = [];
const difficultySettings = {
    1: { filter: 'none', transform: 'none', label: 'Easy' },
    2: { filter: 'grayscale(0%)', transform: 'none', label: 'Medium' },
    3: { filter: 'grayscale(100%)', transform: 'scaleX(-1)', label: 'Challenger' }
};

function updateImageDifficulty(level) {
    const settings = difficultySettings[level];
    const image = document.getElementById('itemImage');
    const label = document.getElementById('difficultyLabel');

    image.style.filter = settings.filter;
    image.style.transform = settings.transform;
    label.textContent = settings.label;
    document.getElementById('currentDifficulty').value = level;
}

function filterItems() {
    const searchText = document.getElementById('searchInput').value.toLowerCase();
    const itemsList = document.getElementById('itemsList');
    itemsList.innerHTML = '';

    const filteredItems = allItems.filter(item =>
        item.name.toLowerCase().includes(searchText)
    ).slice(0, 10);

    filteredItems.forEach(item => {
        const div = document.createElement('div');
        div.className = 'item-option';
        div.textContent = item.name;
        div.onclick = () => selectItem(item);
        itemsList.appendChild(div);
    });
}

function selectItem(item) {
    document.getElementById('selectedItemId').value = item.id;
    document.getElementById('searchInput').value = item.name;
    document.getElementById('itemsList').innerHTML = '';
}

document.addEventListener('DOMContentLoaded', function() {
    const difficultySlider = document.getElementById('difficulty');
    if (difficultySlider) {
        difficultySlider.addEventListener('input', function(e) {
            updateImageDifficulty(e.target.value);
        });
    }

    fetch('/api/items')
        .then(response => response.json())
        .then(items => {
            allItems = items;
            filterItems();
        })
        .catch(error => console.error('Error loading items:', error));
});