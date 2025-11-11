package ru.itis.wr.entities;

public enum ShopItemType {
    ICON("Иконка"),
    BACKGROUND("Фон"),
    BORDER("Рамка"),
    FONT("Шрифт ника");

    private final String displayName;

    ShopItemType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() { return displayName; }
}
