package ru.itis.wr.services;

import ru.itis.wr.entities.*;
import ru.itis.wr.repositories.ShopItemRepository;
import ru.itis.wr.repositories.UserPurchaseRepository;
import ru.itis.wr.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

public class ShopServiceImpl implements ShopService {
    private final ShopItemRepository shopItemRepository;
    private final UserPurchaseRepository userPurchaseRepository;
    private final UserRepository userRepository;

    public ShopServiceImpl(ShopItemRepository shopItemRepository,
                           UserPurchaseRepository userPurchaseRepository,
                           UserRepository userRepository) {
        this.shopItemRepository = shopItemRepository;
        this.userPurchaseRepository = userPurchaseRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<ShopItem> getAvailableItems() {
        return shopItemRepository.findAllActive();
    }

    @Override
    public List<ShopItem> getItemsByType(ShopItemType type) {
        return shopItemRepository.findByType(type);
    }

    @Override
    public PurchaseResult purchaseItem(Long userId, Long shopItemId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return PurchaseResult.error("User not found");
        }

        Optional<ShopItem> itemOpt = shopItemRepository.findById(shopItemId);
        if (itemOpt.isEmpty() || !itemOpt.get().getIsActive()) {
            return PurchaseResult.error("Item not available");
        }

        User user = userOpt.get();
        ShopItem item = itemOpt.get();

        if (userPurchaseRepository.userOwnsItem(userId, shopItemId)) {
            return PurchaseResult.error("You already own this item");
        }

        if (user.getCoins() < item.getPrice()) {
            return PurchaseResult.error("Not enough coins");
        }

        user.setCoins(user.getCoins() - item.getPrice());
        userRepository.update(user);

        UserPurchase purchase = new UserPurchase(userId, shopItemId);
        Long purchaseId = userPurchaseRepository.save(purchase);
        purchase.setId(purchaseId);

        return PurchaseResult.success(purchase, "Item purchased successfully");
    }

    @Override
    public boolean equipItem(Long userId, Long purchaseId) {
        Optional<UserPurchase> purchaseOpt = userPurchaseRepository.findById(purchaseId);
        if (purchaseOpt.isEmpty() || !purchaseOpt.get().getUserId().equals(userId)) {
            return false;
        }

        UserPurchase purchase = purchaseOpt.get();
        Optional<ShopItem> itemOpt = shopItemRepository.findById(purchase.getShopItemId());
        if (itemOpt.isEmpty()) {
            return false;
        }

        ShopItem item = itemOpt.get();

        List<UserPurchase> equippedItems = userPurchaseRepository.findEquippedByUserId(userId);
        for (UserPurchase equipped : equippedItems) {
            Optional<ShopItem> equippedItemOpt = shopItemRepository.findById(equipped.getShopItemId());
            if (equippedItemOpt.isPresent() && equippedItemOpt.get().getType() == item.getType()) {
                equipped.setIsEquipped(false);
                userPurchaseRepository.update(equipped);
            }
        }

        purchase.setIsEquipped(true);
        userPurchaseRepository.update(purchase);

        return true;
    }

    @Override
    public List<UserPurchase> getUserInventory(Long userId) {
        return userPurchaseRepository.findByUserId(userId);
    }

    @Override
    public List<UserPurchase> getEquippedItems(Long userId) {
        return userPurchaseRepository.findEquippedByUserId(userId);
    }

    @Override
    public boolean userOwnsItem(Long userId, Long shopItemId) {
        return userPurchaseRepository.userOwnsItem(userId, shopItemId);
    }
}
