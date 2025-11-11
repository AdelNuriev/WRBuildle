let allItems = [];
let currentAttributes = [];

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
        div.innerHTML = `
            <img src="${item.iconUrl}" alt="${item.name}" class="item-icon-small">
            <span>${item.name}</span>
            <span class="item-cost">${item.cost}g</span>
        `;
        div.onclick = () => selectItem(item);
        itemsList.appendChild(div);
    });
}

function selectItem(item) {
    document.getElementById('selectedItemId').value = item.id;
    document.getElementById('searchInput').value = item.name;
    document.getElementById('itemsList').innerHTML = '';
}

function loadCurrentAttributes() {
    const attributesList = document.getElementById('attributesList');
    if (attributesList) {
        attributesList.innerHTML = `
            <div class="attribute">Стоимость: ???</div>
            <div class="attribute">Редкость: ???</div>
            <div class="attribute">Основные характеристики: ???</div>
        `;
    }
}

document.addEventListener('DOMContentLoaded', function() {
    fetch('/api/items')
        .then(response => response.json())
        .then(items => {
            allItems = items;
            filterItems();
        })
        .catch(error => console.error('Error loading items:', error));

    setTimeout(loadCurrentAttributes, 1000);
});