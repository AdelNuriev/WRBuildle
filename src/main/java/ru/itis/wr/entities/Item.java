package ru.itis.wr.entities;

public class Item {
    private Long id;
    private String name;
    private ItemRarity rarity;
    private Short cost;
    private String iconUrl;
    private ItemAttributes[] attributes;
    boolean isActive;

    public Item() {}

    public Item(Long id,
                String name,
                ItemRarity rarity,
                short cost,
                String iconUrl,
                ItemAttributes[] attributes,
                boolean isActive)
    {
        this.id = id;
        this.name = name;
        this.rarity = rarity;
        this.cost = cost;
        this.iconUrl = iconUrl;
        this.attributes = attributes;
        this.isActive = isActive;
    }

    public ItemAttributes[] getAttributes() { return attributes; }
    public void setAttributes(ItemAttributes[] attributes) { this.attributes = attributes; }
    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }
    public Short getCost() { return cost; }
    public void setCost(Short cost) { this.cost = cost; }
    public ItemRarity getRarity() { return rarity; }
    public void setRarity(ItemRarity rarity) { this.rarity = rarity; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getAttributesAsString() {
        if (attributes == null || attributes.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < attributes.length; i++) {
            sb.append(attributes[i].getDisplayName());
            if (i < attributes.length - 1) sb.append(", ");
        }
        return sb.toString();
    }
}
