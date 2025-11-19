package ru.itis.wr.entities;

public enum BlockType {
    ICON("ICON", "Выглядит знакомым?"),
    CLASSIC("CLASSIC", "Угадай предмет по его сборке"),
    ATTRIBUTES("ATTRIBUTES", "Получай атрибуты предмета с каждой попыткой"),
    MISSING("MISSING", "Что в сборке пропущено?"),
    IMPOSTER("IMPOSTER", "Один предмет здесь явно лишний"),
    COST("COST", "Определи стоимость предмета");

    private final String displayName;
    private final String context;

    BlockType(String displayName, String context) {
        this.displayName = displayName;
        this.context = context;
    }

    public String getDisplayName() { return displayName; }
    public String getContext() { return context; }
}
