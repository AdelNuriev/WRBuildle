package ru.itis.wr.dto;

import ru.itis.wr.entities.ItemAttributes;
import ru.itis.wr.entities.ItemRarity;

public class ItemCreateRequest {
    private String name;
    private ItemRarity rarity;
    private Short cost;
    private String iconUrl;
    private ItemAttributes[] attributes;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public ItemRarity getRarity() { return rarity; }
    public void setRarity(ItemRarity rarity) { this.rarity = rarity; }
    public short getCost() { return cost; }
    public void setCost(Short cost) { this.cost = cost; }
    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }
    public ItemAttributes[] getAttributes() { return attributes; }
    public void setAttributes(ItemAttributes[] attributes) { this.attributes = attributes; }
}
