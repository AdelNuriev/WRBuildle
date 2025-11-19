class ShopManager {
    constructor() {
        this.currentFilter = 'all';
        this.currentSort = 'price_asc';
        this.currentPage = 1;
        this.itemsPerPage = 12;
        this.init();
    }

    init() {
        this.bindEvents();
        this.updateItemsCount();
        this.setupPagination();
    }

    bindEvents() {
        document.querySelectorAll('.category-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const type = this.getTypeFromText(e.target.textContent);
                this.filterItems(type);
            });
        });

        let searchTimeout;
        const searchInput = document.getElementById('searchInput');
        if (searchInput) {
            searchInput.addEventListener('input', (e) => {
                clearTimeout(searchTimeout);
                searchTimeout = setTimeout(() => this.searchItems(e.target.value), 300);
            });
        }

        const sortSelect = document.getElementById('sortSelect');
        if (sortSelect) {
            sortSelect.addEventListener('change', (e) => {
                this.currentSort = e.target.value;
                this.applySorting();
            });
        }

        this.setupModalHandlers();
    }

    getTypeFromText(text) {
        const typeMap = {
            'Все предметы': 'all',
            'Иконки': 'ICON',
            'Фоны': 'BACKGROUND',
            'Рамки': 'BORDER',
            'Шрифты': 'FONT'
        };
        return typeMap[text] || 'all';
    }

    filterItems(type) {
        this.currentFilter = type;
        this.currentPage = 1;

        const items = document.querySelectorAll('.shop-item');
        const buttons = document.querySelectorAll('.category-btn');

        buttons.forEach(btn => btn.classList.remove('active'));
        event.target.classList.add('active');

        let visibleCount = 0;
        items.forEach(item => {
            const itemType = item.dataset.type;
            const isVisible = type === 'all' || itemType === type;

            if (isVisible) {
                item.style.display = 'block';
                visibleCount++;
            } else {
                item.style.display = 'none';
            }
        });

        this.updateShownItems(visibleCount);
        this.setupPagination();
        this.applySorting();
    }

    searchItems(searchTerm) {
        const term = searchTerm.toLowerCase().trim();
        const items = document.querySelectorAll('.shop-item');

        let visibleCount = 0;
        items.forEach(item => {
            const itemName = item.dataset.name;
            const matchesSearch = term === '' || itemName.includes(term);
            const matchesFilter = this.currentFilter === 'all' || item.dataset.type === this.currentFilter;
            const isVisible = matchesSearch && matchesFilter;

            if (isVisible) {
                item.style.display = 'block';
                visibleCount++;
            } else {
                item.style.display = 'none';
            }
        });

        this.updateShownItems(visibleCount);
        this.setupPagination();
    }

    getVisibleItems() {
        return Array.from(document.querySelectorAll('.shop-item')).filter(item => {
            return item.style.display !== 'none';
        });
    }

    applySorting() {
        const container = document.getElementById('shopItemsContainer');
        const visibleItems = this.getVisibleItems();

        visibleItems.sort((a, b) => {
            switch(this.currentSort) {
                case 'price_asc':
                    return parseInt(a.dataset.price) - parseInt(b.dataset.price);
                case 'price_desc':
                    return parseInt(b.dataset.price) - parseInt(a.dataset.price);
                case 'name_asc':
                    return a.dataset.name.localeCompare(b.dataset.name);
                case 'name_desc':
                    return b.dataset.name.localeCompare(a.dataset.name);
                case 'rarity':
                    const rarityOrder = { 'LEGENDARY': 4, 'EPIC': 3, 'RARE': 2, 'COMMON': 1 };
                    return rarityOrder[b.dataset.rarity] - rarityOrder[a.dataset.rarity];
                default:
                    return 0;
            }
        });

        visibleItems.forEach(item => container.appendChild(item));
        this.showPage(this.currentPage);
    }

    setupModalHandlers() {
        document.querySelectorAll('.close-modal, .close-message').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const parent = e.target.closest('.modal, .success-message, .error-message');
                if (parent) {
                    parent.style.display = 'none';
                }
            });
        });

        window.addEventListener('click', (e) => {
            if (e.target.classList.contains('modal')) {
                e.target.style.display = 'none';
            }
        });
    }

    confirmPurchase(price, name, itemId, imageUrl, type) {
        const userCoins = parseInt(document.getElementById('userCoins').textContent);

        if (userCoins < price) {
            alert('Недостаточно монет для покупки!');
            return false;
        }

        this.setupPurchaseModal(price, name, itemId, imageUrl, type);
        return false;
    }

    setupPurchaseModal(price, name, itemId, imageUrl, type) {
        const modal = document.getElementById('purchaseModal');
        const message = `Вы уверены, что хотите купить "${name}" за ${price} монет?`;

        document.getElementById('purchaseMessage').textContent = message;
        document.getElementById('modalItemName').textContent = name;
        document.getElementById('modalItemPrice').textContent = `Цена: ${price} 🪙`;
        document.getElementById('modalItemImage').src = imageUrl;
        document.getElementById('modalItemImage').alt = name;
        document.getElementById('modalItemType').textContent = `Тип: ${this.getTypeDisplayName(type)}`;
        document.getElementById('modalItemId').value = itemId;

        modal.style.display = 'block';
    }

    getTypeDisplayName(type) {
        const typeMap = {
            'ICON': 'Иконка',
            'BACKGROUND': 'Фон',
            'BORDER': 'Рамка',
            'FONT': 'Шрифт'
        };
        return typeMap[type] || type;
    }

    updateItemsCount() {
        const totalItems = document.querySelectorAll('.shop-item').length;
        const ownedItems = document.querySelectorAll('.owned-badge').length;

        const totalItemsElement = document.getElementById('totalItems');
        const ownedItemsElement = document.getElementById('ownedItems');

        if (totalItemsElement) totalItemsElement.textContent = totalItems;
        if (ownedItemsElement) ownedItemsElement.textContent = ownedItems;
        this.updateShownItems(totalItems);
    }

    updateShownItems(count) {
        const shownItemsElement = document.getElementById('shownItems');
        if (shownItemsElement) {
            shownItemsElement.textContent = count;
        }
    }

    setupPagination() {
        const visibleItems = this.getVisibleItems();
        const totalPages = Math.ceil(visibleItems.length / this.itemsPerPage);
        const pagination = document.getElementById('pagination');

        if (!pagination) return;

        pagination.innerHTML = '';

        if (totalPages <= 1) {
            this.showPage(1);
            return;
        }

        const prevBtn = document.createElement('button');
        prevBtn.className = 'page-btn prev';
        prevBtn.innerHTML = '&laquo;';
        prevBtn.onclick = () => this.goToPage(this.currentPage - 1);
        prevBtn.disabled = this.currentPage === 1;
        pagination.appendChild(prevBtn);

        for (let i = 1; i <= totalPages; i++) {
            const pageBtn = document.createElement('button');
            pageBtn.className = `page-btn ${i === this.currentPage ? 'active' : ''}`;
            pageBtn.textContent = i;
            pageBtn.onclick = () => this.goToPage(i);
            pagination.appendChild(pageBtn);
        }

        const nextBtn = document.createElement('button');
        nextBtn.className = 'page-btn next';
        nextBtn.innerHTML = '&raquo;';
        nextBtn.onclick = () => this.goToPage(this.currentPage + 1);
        nextBtn.disabled = this.currentPage === totalPages;
        pagination.appendChild(nextBtn);

        this.showPage(this.currentPage);
    }

    goToPage(page) {
        this.currentPage = page;
        this.showPage(page);
        this.updatePaginationButtons();
    }

    showPage(page) {
        const visibleItems = this.getVisibleItems();
        const startIndex = (page - 1) * this.itemsPerPage;
        const endIndex = startIndex + this.itemsPerPage;

        visibleItems.forEach(item => {
            item.style.display = 'none';
        });

        visibleItems.forEach((item, index) => {
            if (index >= startIndex && index < endIndex) {
                item.style.display = 'block';
            }
        });
    }

    updatePaginationButtons() {
        const pageBtns = document.querySelectorAll('.page-btn');
        const visibleItems = this.getVisibleItems();
        const totalPages = Math.ceil(visibleItems.length / this.itemsPerPage);

        pageBtns.forEach(btn => {
            btn.classList.remove('active');
            if (btn.textContent === this.currentPage.toString() && !btn.classList.contains('prev') && !btn.classList.contains('next')) {
                btn.classList.add('active');
            }

            if (btn.classList.contains('prev')) {
                btn.disabled = this.currentPage === 1;
            }
            if (btn.classList.contains('next')) {
                btn.disabled = this.currentPage === totalPages;
            }
        });
    }
}

document.addEventListener('DOMContentLoaded', function() {
    window.shopManager = new ShopManager();
});