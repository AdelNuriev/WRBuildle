let attemptsChart, scoreChart;

function updateCharts() {
    const period = document.getElementById('periodSelect').value;
    const blockType = document.getElementById('blockTypeSelect').value;

    fetch(`/statistics/graph?blockType=${blockType}&days=${period}`)
        .then(response => response.json())
        .then(data => {
            renderCharts(data.data);
        })
        .catch(error => console.error('Error loading chart data:', error));
}

function renderCharts(graphData) {
    if (attemptsChart) attemptsChart.destroy();
    if (scoreChart) scoreChart.destroy();

    const attemptsCtx = document.getElementById('attemptsChart').getContext('2d');
    attemptsChart = new Chart(attemptsCtx, {
        type: 'line',
        data: {
            labels: graphData.labels,
            datasets: [{
                label: 'Попытки',
                data: graphData.attempts,
                borderColor: '#c8aa6e',
                backgroundColor: 'rgba(200, 170, 110, 0.1)',
                tension: 0.4,
                fill: true
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    labels: {
                        color: '#f0e6d2'
                    }
                }
            },
            scales: {
                x: {
                    ticks: {
                        color: '#a09b8c'
                    },
                    grid: {
                        color: '#463714'
                    }
                },
                y: {
                    ticks: {
                        color: '#a09b8c'
                    },
                    grid: {
                        color: '#463714'
                    }
                }
            }
        }
    });

    const scoreCtx = document.getElementById('scoreChart').getContext('2d');
    scoreChart = new Chart(scoreCtx, {
        type: 'bar',
        data: {
            labels: graphData.labels,
            datasets: [{
                label: 'Очки',
                data: graphData.score,
                backgroundColor: '#785a28',
                borderColor: '#c8aa6e',
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    labels: {
                        color: '#f0e6d2'
                    }
                }
            },
            scales: {
                x: {
                    ticks: {
                        color: '#a09b8c'
                    },
                    grid: {
                        color: '#463714'
                    }
                },
                y: {
                    ticks: {
                        color: '#a09b8c'
                    },
                    grid: {
                        color: '#463714'
                    }
                }
            }
        }
    });
}

document.addEventListener('DOMContentLoaded', function() {
    const periodSelect = document.getElementById('periodSelect');
    const blockTypeSelect = document.getElementById('blockTypeSelect');

    if (periodSelect && blockTypeSelect) {
        periodSelect.addEventListener('change', updateCharts);
        blockTypeSelect.addEventListener('change', updateCharts);
        updateCharts();
    }
});