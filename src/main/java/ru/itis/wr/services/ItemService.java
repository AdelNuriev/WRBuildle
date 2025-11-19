package ru.itis.wr.services;

import ru.itis.wr.entities.Item;
import ru.itis.wr.entities.ItemRarity;
import ru.itis.wr.entities.ItemRecipe;
import ru.itis.wr.entities.ItemTree;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    List<Item> getAllItems();
    Optional<Item> getItemById(Long id);
    void addRecipe(ItemRecipe recipe);
    void removeRecipe(Long parentItemId, Long componentItemId);
    ItemTree getItemTree(Long itemId);
    List<Item> getItemComponents(Long itemId);
    public ItemTree getFullItemTree(Long itemId);
    void saveRecipeTree(Long rootItemId, ItemTree tree);
    void clearItemRecipe(Long itemId);
    Item createItem(Item item);
    Item updateItem(Item item);
    void deleteItem(Long itemId);
}
