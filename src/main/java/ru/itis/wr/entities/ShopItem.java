package ru.itis.wr.entities;

import java.time.LocalDateTime;

public class ShopItem {
    private Long id;
    private String name;
    private ShopItemType type;
    private Integer price;
    private String imageUrl;
    private ShopItemRarity rarity;
    private Boolean isActive;
    private LocalDateTime createdAt;

    public ShopItem() {}

    public ShopItem(Long id, String name, ShopItemType type, Integer price, String imageUrl) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.price = price;
        this.imageUrl = imageUrl;
        this.rarity = ShopItemRarity.COMMON;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }

    public ShopItem(Long id,
                    String name,
                    ShopItemType type,
                    Integer price,
                    String imageUrl,
                    ShopItemRarity rarity,
                    Boolean isActive,
                    LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.price = price;
        this.imageUrl = imageUrl;
        this.rarity = rarity;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public ShopItemType getType() { return type; }
    public void setType(ShopItemType type) { this.type = type; }
    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public ShopItemRarity getRarity() { return rarity; }
    public void setRarity(ShopItemRarity rarity) { this.rarity = rarity; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
