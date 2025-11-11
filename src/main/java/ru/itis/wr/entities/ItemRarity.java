package ru.itis.wr.entities;

public enum ItemRarity {
    COMMON("Base", "#ABCDEF"),
    BOOT("Boot", "#44944A"),
    EPIC("Epic", "#660099"),
    MYTHICAL("Mythical", "#F80000"),
    LEGENDARY("Legendary", "#ffd700");

    private final String displayName;
    private final String color;

    ItemRarity(String displayName, String color) {
        this.displayName = displayName;
        this.color = color;
    }

    public String getDisplayName() { return displayName; }
    public String getColor() { return color; }
}
