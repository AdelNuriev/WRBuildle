let allItems = [];
let currentTree = null;
let imposterItemId = null;

function renderTreeWithImposter(tree, parentElement, depth = 0) {
    const node = document.createElement('div');
    node.className = `tree-node depth-${depth}`;

    const itemInfo = document.createElement('div');
    itemInfo.className = 'tree-item';

    if (tree.item) {
        if (tree.item.id === imposterItemId) {
            itemInfo.className += ' imposter-item';
        }

        itemInfo.innerHTML = `
            <img src="${tree.item.iconUrl}" alt="${tree.item.name}" class="item-icon">
            <span>${tree.item.name}</span>
            <span class="item-cost">${tree.item.cost}g</span>
            ${tree.item.id === imposterItemId ? '<div class="imposter-mark">?</div>' : ''}
        `;
    } else {
        itemInfo.innerHTML = `<div class="unknown-item">???</div>`;
    }

    node.appendChild(itemInfo);

    if (tree.hasComponents) {
        const components = document.createElement('div');
        components.className = 'tree-components';

        tree.components.forEach(component => {
            renderTreeWithImposter(component, components, depth + 1);
        });

        node.appendChild(components);
    }

    parentElement.appendChild(node);
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

document.addEventListener('DOMContentLoaded', function() {
    try {
        currentTree = JSON.parse(document.body.getAttribute('data-tree') || '{}');
        imposterItemId = parseInt(document.body.getAttribute('data-imposter-item') || '0');
        renderTreeWithImposter(currentTree, document.getElementById('treeContainer'));
    } catch (e) {
        console.error('Error parsing tree data:', e);
    }

    fetch('/api/items')
        .then(response => response.json())
        .then(items => {
            allItems = items;
            filterItems();
        })
        .catch(error => console.error('Error loading items:', error));
});