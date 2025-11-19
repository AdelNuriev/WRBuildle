package ru.itis.wr.repositories;

import ru.itis.wr.entities.Item;
import ru.itis.wr.entities.ItemRarity;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Long save(Item item);
    Optional<Item> findById(Long id);
    List<Item> findAll();
    List<Item> findByNameContaining(String name);
    List<Item> findByRarity(ItemRarity rarity);
    List<Item> findByCostRange(Integer minCost, Integer maxCost);
    void update(Item item);
    void deactivate(Long itemId);
}
