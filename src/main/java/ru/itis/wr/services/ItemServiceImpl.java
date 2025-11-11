package ru.itis.wr.services;

import ru.itis.wr.entities.Item;
import ru.itis.wr.entities.ItemRarity;
import ru.itis.wr.entities.ItemTree;
import ru.itis.wr.entities.ItemRecipe;
import ru.itis.wr.repositories.ItemRepository;
import ru.itis.wr.repositories.ItemRecipeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemRecipeRepository itemRecipeRepository;

    public ItemServiceImpl(ItemRepository itemRepository, ItemRecipeRepository itemRecipeRepository) {
        this.itemRepository = itemRepository;
        this.itemRecipeRepository = itemRecipeRepository;
    }

    @Override
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @Override
    public Optional<Item> getItemById(Long id) {
        return itemRepository.findById(id);
    }

    @Override
    public List<Item> searchItems(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.findByNameContaining(query.trim());
    }

    @Override
    public List<Item> getItemsByRarity(ItemRarity rarity) {
        return itemRepository.findByRarity(rarity);
    }

    @Override
    public List<Item> getItemsByCostRange(int minCost, int maxCost) {
        return itemRepository.findByCostRange(minCost, maxCost);
    }

    @Override
    public ItemTree getItemTree(Long itemId) {
        Optional<Item> itemOpt = itemRepository.findById(itemId);
        if (itemOpt.isEmpty()) {
            throw new IllegalArgumentException("Item not found");
        }

        Item rootItem = itemOpt.get();
        ItemTree tree = new ItemTree(rootItem);

        // Рекурсивно строим дерево компонентов
        buildItemTree(tree, rootItem.getId());

        return tree;
    }

    @Override
    public List<Item> getItemComponents(Long itemId) {
        List<ItemRecipe> recipes = itemRecipeRepository.findByParentItemId(itemId);
        List<Item> components = new ArrayList<>();

        for (ItemRecipe recipe : recipes) {
            itemRepository.findById(recipe.getComponentItemId())
                    .ifPresent(components::add);
        }

        return components;
    }

    @Override
    public List<Item> getItemsBuiltFrom(Long componentId) {
        List<ItemRecipe> recipes = itemRecipeRepository.findByComponentItemId(componentId);
        List<Item> items = new ArrayList<>();

        for (ItemRecipe recipe : recipes) {
            itemRepository.findById(recipe.getParentItemId())
                    .ifPresent(items::add);
        }

        return items;
    }

    @Override
    public Item createItem(Item item) {
        if (item.getName() == null || item.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Item name is required");
        }

        if (item.getCost() == null || item.getCost() < 0) {
            throw new IllegalArgumentException("Item cost must be non-negative");
        }

        Long itemId = itemRepository.save(item);
        item.setId(itemId);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        if (item.getId() == null) {
            throw new IllegalArgumentException("Item ID is required for update");
        }

        Optional<Item> existingItem = itemRepository.findById(item.getId());
        if (existingItem.isEmpty()) {
            throw new IllegalArgumentException("Item not found");
        }

        itemRepository.update(item);
        return item;
    }

    @Override
    public void deleteItem(Long itemId) {
        itemRepository.deactivate(itemId);
    }

    private void buildItemTree(ItemTree tree, Long itemId) {
        List<ItemRecipe> recipes = itemRecipeRepository.findByParentItemId(itemId);

        for (ItemRecipe recipe : recipes) {
            Optional<Item> componentOpt = itemRepository.findById(recipe.getComponentItemId());
            if (componentOpt.isPresent()) {
                Item component = componentOpt.get();
                ItemTree childTree = new ItemTree(component);
                tree.addComponent(childTree);

                buildItemTree(childTree, component.getId());
            }
        }
    }
}
