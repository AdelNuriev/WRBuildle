function showCreateForm() {
    document.getElementById('createForm').style.display = 'block';
}

function hideCreateForm() {
    document.getElementById('createForm').style.display = 'none';
}

function editItem(itemId) {
    alert('Редактирование предмета ID: ' + itemId);
}

document.addEventListener('DOMContentLoaded', function() {
    const deactivateForms = document.querySelectorAll('form[action*="update-item"]');
    deactivateForms.forEach(form => {
        form.addEventListener('submit', function(e) {
            if (!confirm('Вы уверены, что хотите деактивировать этот предмет?')) {
                e.preventDefault();
            }
        });
    });
});