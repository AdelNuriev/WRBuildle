function submitGuess() {
    const costInput = document.getElementById('costInput');
    const guessedCost = parseInt(costInput.value);

    if (isNaN(guessedCost) || guessedCost < 0 || guessedCost > 5000) {
        alert('Пожалуйста, введите корректную стоимость (0-5000)');
        return;
    }

    document.getElementById('guessedCostValue').value = guessedCost;
    document.querySelector('.guess-form').submit();
}

function updateCostHint(cost) {
    let hint = '';
    if (cost < 300) hint = 'Очень дешево - возможно базовый предмет';
    else if (cost < 800) hint = 'Низкая стоимость - вероятно компонент';
    else if (cost < 1300) hint = 'Средняя стоимость - эпический предмет или сапоги';
    else if (cost < 2000) hint = 'Высокая стоимость - мифический предмет';
    else if (cost < 3000) hint = 'Очень высокая стоимость - легендарный предмет';
    else hint = 'Максимальная стоимость - топовый легендарный предмет';

    console.log(hint);
}

document.addEventListener('DOMContentLoaded', function() {
    const costInput = document.getElementById('costInput');
    if (costInput) {
        costInput.addEventListener('input', function(e) {
            const value = parseInt(e.target.value);
            if (!isNaN(value)) {
                updateCostHint(value);
            }
        });
    }
});