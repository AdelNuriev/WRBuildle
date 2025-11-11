package ru.itis.wr.entities;

public enum Role {
    USER("Пользователь"),
    ADMIN("Админ");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() { return displayName; }
}
