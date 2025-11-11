function showCreateForm() {
    document.getElementById('createForm').style.display = 'block';
}

function hideCreateForm() {
    document.getElementById('createForm').style.display = 'none';
}

function editItem(itemId) {
    // Реализация редактирования предмета
    alert('Редактирование предмета ID: ' + itemId);
    // В реальном приложении здесь будет открытие модального окна или переход на страницу редактирования
}

document.addEventListener('DOMContentLoaded', function() {
    // Добавляем обработчики для форм деактивации
    const deactivateForms = document.querySelectorAll('form[action*="update-item"]');
    deactivateForms.forEach(form => {
        form.addEventListener('submit', function(e) {
            if (!confirm('Вы уверены, что хотите деактивировать этот предмет?')) {
                e.preventDefault();
            }
        });
    });
});