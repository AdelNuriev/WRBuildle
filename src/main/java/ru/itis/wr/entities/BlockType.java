package ru.itis.wr.entities;

public enum BlockType {
    ICON("Иконка", "Выглядит знакомым?"),
    CLASSIC("Классика", "Угадай предмет по его сборке"),
    ATTRIBUTES("Атрибуты", "Получай атрибуты предмета с каждой попыткой"),
    MISSING("Пропуск", "Что в сборке пропущено?"),
    IMPOSTER("Предатель", "Один предмет здесь явно лишний"),
    COST("Стоимость", "Определи стоимость предмета");

    private final String displayName;
    private final String context;

    private BlockType(String displayName, String context) {
        this.displayName = displayName;
        this.context = context;
    }

    public String getDisplayName() { return displayName; }
    public String getContext() { return context; }
}
