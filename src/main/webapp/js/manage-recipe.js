let allItems = [];
let selectedTreeNode = null;
let selectedComponent = null;
let branchCounter = 0;

window.addRootComponent = addRootComponent;
window.saveRecipeTree = saveRecipeTree;
window.searchItems = searchItems;
window.initializeRecipeTree = initializeRecipeTree;
window.loadAllItems = loadAllItems;

document.addEventListener('DOMContentLoaded', function() {
    if (!window.CURRENT_ITEM) {
        console.error('CURRENT_ITEM not defined in window object');
        return;
    }
    loadAllItems();
});

function loadAllItems() {
    console.log('Loading all items...');
    fetch('/admin/api/items')
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(items => {
            allItems = items.map(item => ({
                ...item,
                id: safeParseInt(item.id)
            })).filter(item => item.id !== window.CURRENT_ITEM.id);
            renderComponentsGrid(allItems);
        })
        .catch(error => console.error('Error loading items:', error));
}

function initializeRecipeTree() {
    console.log('Initializing recipe tree for item:', window.CURRENT_ITEM);

    if (!window.CURRENT_ITEM) {
        console.error('CURRENT_ITEM not defined');
        return;
    }

    fetch('/admin/api/full-item-tree?itemId=' + window.CURRENT_ITEM.id)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(treeData => {
            console.log('Loaded tree data:', treeData);
            if (treeData && treeData.components && treeData.components.length > 0) {
                renderExistingTree(treeData);
            } else {
                const rootContainer = document.getElementById('rootComponents');
                addEmptyBranch(rootContainer);
            }
        })
        .catch(error => {
            console.error('Error loading tree:', error);
            const rootContainer = document.getElementById('rootComponents');
            addEmptyBranch(rootContainer);
        });
}

function renderExistingTree(treeData) {
    const rootContainer = document.getElementById('rootComponents');
    if (!rootContainer) {
        console.error('rootComponents container not found');
        return;
    }

    rootContainer.innerHTML = '';

    if (treeData.components && treeData.components.length > 0) {
        treeData.components.forEach(component => {
            const branch = createComponentBranch(component);
            rootContainer.appendChild(branch);
        });
    }
}

function createComponentBranch(component, parentBranch = null) {
    const branchId = 'branch_' + branchCounter++;
    const branch = document.createElement('div');
    branch.className = 'tree-branch';
    branch.id = branchId;

    const node = createTreeNode(component.item, false);
    branch.appendChild(node);

    const childrenContainer = document.createElement('div');
    childrenContainer.className = 'branch-children';

    if (component.components && component.components.length > 0) {
        component.components.forEach(childComponent => {
            const childBranch = createComponentBranch(childComponent, branch);
            childrenContainer.appendChild(childBranch);
        });
    }

    addEmptyBranch(childrenContainer);

    branch.appendChild(childrenContainer);
    addRemoveButton(branch);

    return branch;
}

function createTreeNode(item, isRoot = false, quantity = 1) {
    const node = document.createElement('div');
    node.className = `tree-node ${isRoot ? 'root-node' : ''}`;
    node.setAttribute('data-item-id', item.id);

    const icon = document.createElement('img');
    icon.className = 'node-icon';
    icon.src = item.iconUrl || '';
    icon.alt = item.name || '';

    const name = document.createElement('span');
    name.className = 'node-name';
    name.textContent = item.name || 'Unknown';

    if (quantity > 1) {
        const quantityBadge = document.createElement('span');
        quantityBadge.className = 'quantity-badge';
        quantityBadge.textContent = `×${quantity}`;
        quantityBadge.style.cssText = `
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
        `;
        node.style.position = 'relative';
        node.appendChild(quantityBadge);
    }

    node.appendChild(icon);
    node.appendChild(name);

    node.addEventListener('click', (e) => {
        e.stopPropagation();
        selectTreeNode(node);
    });

    return node;
}

function createEmptyNode() {
    const node = document.createElement('div');
    node.className = 'tree-node empty';
    node.innerHTML = '<span>+</span>';
    node.addEventListener('click', (e) => {
        e.stopPropagation();
        selectTreeNode(node);
    });
    return node;
}

function addEmptyBranch(container) {
    if (!container) return;

    const emptyBranch = document.createElement('div');
    emptyBranch.className = 'tree-branch';

    const emptyNode = createEmptyNode();
    emptyBranch.appendChild(emptyNode);

    const emptyChildren = document.createElement('div');
    emptyChildren.className = 'branch-children';
    emptyBranch.appendChild(emptyChildren);

    container.appendChild(emptyBranch);
}

function addRemoveButton(branch) {
    const actions = document.createElement('div');
    actions.className = 'branch-actions';

    const removeBtn = document.createElement('button');
    removeBtn.className = 'btn-remove';
    removeBtn.innerHTML = '×';
    removeBtn.title = 'Удалить ветку';
    removeBtn.addEventListener('click', (e) => {
        e.stopPropagation();
        removeBranch(branch);
    });

    actions.appendChild(removeBtn);
    branch.appendChild(actions);
}

function addRootComponent() {
    console.log('Adding root component');
    const rootContainer = document.getElementById('rootComponents');
    if (rootContainer) {
        addEmptyBranch(rootContainer);
        showNotification('Добавлена новая корневая ветка', 'success');
    }
}

function selectTreeNode(node) {
    if (!node) return;

    document.querySelectorAll('.tree-node').forEach(n => {
        if (n) {
            n.classList.remove('selected');
            n.classList.remove('highlight');
        }
    });

    node.classList.add('selected');
    selectedTreeNode = node;

    const selectionInfo = document.getElementById('selectionInfo');
    if (!selectionInfo) return;

    if (node.classList.contains('empty')) {
        selectionInfo.textContent = 'Выбран пустой узел. Выберите компонент снизу для добавления.';
        selectionInfo.style.color = '#c8aa6e';
    } else {
        const itemId = safeParseInt(node.getAttribute('data-item-id'));
        const item = allItems.find(i => i.id === itemId) || window.CURRENT_ITEM;
        selectionInfo.textContent = `Выбран: ${item?.name || 'Unknown'}. Выберите компонент для добавления в эту ветку.`;
        selectionInfo.style.color = '#f0e6d2';
    }
}

function selectComponent(card, item) {
    if (!card || !item) return;

    document.querySelectorAll('.component-card').forEach(c => {
        if (c) c.classList.remove('selected');
    });

    card.classList.add('selected');
    selectedComponent = item;

    if (selectedTreeNode && selectedTreeNode.classList.contains('empty')) {
        addComponentToNode(item);
    } else if (selectedTreeNode) {
        addChildComponent(selectedTreeNode, item);
    }
}

function addComponentToNode(item) {
    if (!selectedTreeNode || !selectedComponent) {
        showNotification('Сначала выберите узел и компонент', 'error');
        return;
    }

    const branch = selectedTreeNode.parentElement;
    if (!branch) return;

    selectedTreeNode.classList.remove('empty');
    selectedTreeNode.innerHTML = '';

    const icon = document.createElement('img');
    icon.className = 'node-icon';
    icon.src = item.iconUrl || '';
    icon.alt = item.name || '';

    const name = document.createElement('span');
    name.className = 'node-name';
    name.textContent = item.name || 'Unknown';

    selectedTreeNode.appendChild(icon);
    selectedTreeNode.appendChild(name);
    selectedTreeNode.setAttribute('data-item-id', safeParseInt(item.id));

    selectedTreeNode.addEventListener('click', (e) => {
        e.stopPropagation();
        selectTreeNode(selectedTreeNode);
    });

    addRemoveButton(branch);

    const childrenContainer = branch.querySelector('.branch-children');
    if (childrenContainer) {
        addEmptyBranch(childrenContainer);
    }

    clearSelection();

    showNotification(`Компонент "${item.name || 'Unknown'}" добавлен в сборку`, 'success');
}

function addChildComponent(parentNode, item) {
    if (!parentNode || !item) return;

    const branch = parentNode.parentElement;
    if (!branch) return;

    const childrenContainer = branch.querySelector('.branch-children');
    if (!childrenContainer) return;

    const newBranch = document.createElement('div');
    newBranch.className = 'tree-branch';

    const newNode = createTreeNode(item, false);
    newBranch.appendChild(newNode);

    const newChildrenContainer = document.createElement('div');
    newChildrenContainer.className = 'branch-children';
    newBranch.appendChild(newChildrenContainer);

    addEmptyBranch(newChildrenContainer);

    addRemoveButton(newBranch);

    childrenContainer.appendChild(newBranch);

    showNotification(`Компонент "${item.name || 'Unknown'}" добавлен как дочерний`, 'success');
    clearSelection();
}

function removeBranch(branch) {
    if (!branch) return;

    if (confirm('Удалить эту ветку сборки?')) {
        branch.remove();
        showNotification('Ветка удалена', 'success');
    }
}

function renderComponentsGrid(items) {
    const grid = document.getElementById('componentsGrid');
    if (!grid) return;

    grid.innerHTML = '';

    items.forEach(item => {
        const card = createComponentCard(item);
        grid.appendChild(card);
    });
}

function createComponentCard(item) {
    const card = document.createElement('div');
    card.className = 'component-card';
    card.setAttribute('data-item-id', safeParseInt(item.id));

    card.innerHTML = `
        <img src="${item.iconUrl || ''}" alt="${item.name || ''}" class="component-icon">
        <div class="component-name">${item.name || 'Unknown'}</div>
        <div class="component-cost">${item.cost || 0}g</div>
    `;

    card.addEventListener('click', () => selectComponent(card, item));
    return card;
}

function searchItems() {
    const searchInput = document.getElementById('searchInput');
    if (!searchInput) return;

    const searchText = searchInput.value.toLowerCase();
    const filteredItems = allItems.filter(item =>
        item.name && item.name.toLowerCase().includes(searchText)
    );

    renderComponentsGrid(filteredItems);
}

function clearSelection() {
    selectedTreeNode = null;
    selectedComponent = null;

    document.querySelectorAll('.tree-node').forEach(n => {
        if (n) n.classList.remove('selected');
    });
    document.querySelectorAll('.component-card').forEach(c => {
        if (c) c.classList.remove('selected');
    });

    const selectionInfo = document.getElementById('selectionInfo');
    if (selectionInfo) {
        selectionInfo.textContent = 'Выберите узел в дереве сверху для добавления компонента';
        selectionInfo.style.color = '#c8aa6e';
    }
}

function saveRecipeTree() {
    console.log('=== SAVE RECIPE TREE START ===');

    if (!window.CURRENT_ITEM) {
        console.error('CURRENT_ITEM not defined');
        showNotification('Ошибка: не определен текущий предмет', 'error');
        return;
    }

    const treeData = collectTreeData();
    console.log('Tree data to save:', treeData);

    if (!treeData || !treeData.item || !treeData.item.id) {
        console.error('Invalid tree data:', treeData);
        showNotification('Ошибка: некорректные данные дерева', 'error');
        return;
    }

    const requestData = {
        itemId: window.CURRENT_ITEM.id,
        recipeData: treeData
    };

    console.log('Sending request with data:', requestData);

    fetch('/admin/save-recipe-tree', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json; charset=UTF-8',
        },
        body: JSON.stringify(requestData)
    })
        .then(response => {
            console.log('Response status:', response.status, response.statusText);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(result => {
            console.log('Server result:', result);
            if (result.success) {
                showNotification(result.message, 'success');
            } else {
                showNotification(result.message, 'error');
            }
        })
        .catch(error => {
            console.error('Fetch error:', error);
            showNotification('Ошибка сети: ' + error.message, 'error');
        });
}

function collectTreeData() {
    const treeData = {
        item: {
            id: window.CURRENT_ITEM.id,
            name: window.CURRENT_ITEM.name || '',
            iconUrl: window.CURRENT_ITEM.iconUrl || '',
            cost: window.CURRENT_ITEM.cost || 0
        },
        components: []
    };

    const rootContainer = document.getElementById('rootComponents');
    if (rootContainer) {
        treeData.components = collectComponentsFromContainer(rootContainer);
    }

    console.log('Collected tree data with duplicates:', treeData);
    return treeData;
}

function collectComponentsFromContainer(container) {
    const components = [];
    const branches = container.querySelectorAll('.tree-branch');

    branches.forEach(branch => {
        const node = branch.querySelector('.tree-node:not(.empty)');
        if (node) {
            const itemId = Number(node.getAttribute('data-item-id'));
            const item = allItems.find(i => i.id === itemId);

            if (item) {
                const component = {
                    item: {
                        id: item.id,
                        name: item.name || '',
                        iconUrl: item.iconUrl || '',
                        cost: item.cost || 0
                    },
                    components: []
                };

                const childrenContainer = branch.querySelector('.branch-children');
                if (childrenContainer) {
                    component.components = collectComponentsFromContainer(childrenContainer);
                }

                components.push(component);
            }
        }
    });

    return components;
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
        ${type === 'success' ? 'background: #27ae60;' : 'background: #e74c3c;'}
    `;

    document.body.appendChild(notification);

    setTimeout(() => {
        if (notification.parentNode) {
            notification.remove();
        }
    }, 3000);
}