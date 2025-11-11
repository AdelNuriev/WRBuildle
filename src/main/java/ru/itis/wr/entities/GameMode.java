package ru.itis.wr.entities;

public enum GameMode {
    DAILY("Ежедневный", "Получите удовольствие в ежедневном режиме"),
    INFINITE("Бесконечный", "Зарабатывайте очки и покажите лучший результат");

    private final String displayName;
    private final String context;

    GameMode(String displayName, String context) {
        this.displayName = displayName;
        this.context = context;
    }

    public String getDisplayName() { return displayName; }
    public String getContext() { return context; }
}
