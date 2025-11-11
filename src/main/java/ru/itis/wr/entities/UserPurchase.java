package ru.itis.wr.entities;

import java.time.LocalDateTime;

public class UserPurchase {
    private Long id;
    private Long userId;
    private Long shopItemId;
    private LocalDateTime purchasedAt;
    private Boolean isEquipped;

    public UserPurchase() {}

    public UserPurchase(Long userId, Long shopItemId) {
        this.userId = userId;
        this.shopItemId = shopItemId;
        this.purchasedAt = LocalDateTime.now();
        this.isEquipped = false;
    }

    public UserPurchase(Long id,
                        Long userId,
                        Long shopItemId,
                        LocalDateTime purchasedAt,
                        Boolean isEquipped) {
        this.id = id;
        this.userId = userId;
        this.shopItemId = shopItemId;
        this.purchasedAt = purchasedAt;
        this.isEquipped = isEquipped;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getShopItemId() { return shopItemId; }
    public void setShopItemId(Long shopItemId) { this.shopItemId = shopItemId; }
    public LocalDateTime getPurchasedAt() { return purchasedAt; }
    public void setPurchasedAt(LocalDateTime purchasedAt) { this.purchasedAt = purchasedAt; }
    public Boolean getIsEquipped() { return isEquipped; }
    public void setIsEquipped(Boolean isEquipped) { this.isEquipped = isEquipped; }
}