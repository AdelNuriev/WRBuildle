package ru.itis.wr.entities;

public enum Role {
    USER("USER"),
    ADMIN("ADMIN");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() { return displayName; }
}
