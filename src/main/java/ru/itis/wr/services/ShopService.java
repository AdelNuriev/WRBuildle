package ru.itis.wr.services;

import ru.itis.wr.entities.ShopItem;
import ru.itis.wr.entities.UserPurchase;
import ru.itis.wr.entities.ShopItemType;

import java.util.List;

public interface ShopService {
    List<ShopItem> getAvailableItems();
    List<ShopItem> getItemsByType(ShopItemType type);
    PurchaseResult purchaseItem(Long userId, Long shopItemId);
    boolean equipItem(Long userId, Long purchaseId);
    List<UserPurchase> getUserInventory(Long userId);
    List<UserPurchase> getEquippedItems(Long userId);
    boolean userOwnsItem(Long userId, Long shopItemId);
}
