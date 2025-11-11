package ru.itis.wr.repositories;

import ru.itis.wr.entities.ShopItem;
import ru.itis.wr.entities.ShopItemRarity;
import ru.itis.wr.entities.ShopItemType;

import java.util.List;
import java.util.Optional;

public interface ShopItemRepository {
    Long save(ShopItem item);
    Optional<ShopItem> findById(Long id);
    List<ShopItem> findAllActive();
    List<ShopItem> findByType(ShopItemType type);
    List<ShopItem> findByRarity(ShopItemRarity rarity);
    void deactivate(Long itemId);
}
