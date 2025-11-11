package ru.itis.wr.entities;

public enum ItemAttributes {
    ATTACK_DAMAGE("AD"),
    CRITICAL_STRIKE_CHANCE("Crit"),
    ATTACK_SPEED("AS"),
    ON_HIT_EFFECTS("On-hit"),
    ARMOR_PENETRATION("Lethality"),
    ABILITY_POWER("AP"),
    MANA("Mana"),
    MANA_RESTORATION("Mana rest"),
    MAGICAL_PENETRATION("MP"),
    HEALTH("HP"),
    HEALTH_RESTORATION("HP rest"),
    ARMOR("Armor"),
    MAGIC_RESISTANCE("MR"),
    ABILITY_HASTE("AH"),
    MOVE_SPEED("MS"),
    PHYSICAL_VAMPIRISM("Vamp"),
    OMNIVAMP("Omni");

    private final String displayName;

    ItemAttributes(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
