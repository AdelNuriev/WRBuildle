package ru.itis.wr.entities;

public enum ShopItemRarity {
    COMMON("Common", "#ABCDEF"),
    RARE("Rare", "#44944A"),
    EPIC("Epic", "#660099"),
    LEGENDARY("Legendary", "#ffd700");

    private final String displayName;
    private final String color;

    ShopItemRarity(String displayName, String color) {
        this.displayName = displayName;
        this.color = color;
    }

    public String getDisplayName() { return displayName; }
    public String getColor() { return color; }
}
