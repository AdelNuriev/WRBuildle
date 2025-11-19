package ru.itis.wr.services;

import ru.itis.wr.entities.Item;
import ru.itis.wr.entities.ItemRarity;
import ru.itis.wr.entities.ItemTree;
import ru.itis.wr.entities.ItemRecipe;
import ru.itis.wr.repositories.ItemRepository;
import ru.itis.wr.repositories.ItemRecipeRepository;

import java.util.*;

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
    public void addRecipe(ItemRecipe recipe) {
        if (recipe.getParentItemId().equals(recipe.getComponentItemId())) {
            throw new IllegalArgumentException("Cannot add item as component to itself");
        }

        if (recipe.getQuantity() == null) {
            recipe.setQuantity(1);
        }

        itemRecipeRepository.save(recipe);
    }

    @Override
    public void removeRecipe(Long parentItemId, Long componentItemId) {
        itemRecipeRepository.deleteByParentAndComponent(parentItemId, componentItemId);
    }

    @Override
    public ItemTree getItemTree(Long itemId) {
        Optional<Item> itemOpt = itemRepository.findById(itemId);
        if (itemOpt.isEmpty()) {
            throw new IllegalArgumentException("Item not found");
        }

        Item rootItem = itemOpt.get();
        if (rootItem.getIconUrl() != null && rootItem.getIconUrl().contains("'")) {
            rootItem.setIconUrl(rootItem.getIconUrl().replace("'", ""));
        }

        ItemTree tree = new ItemTree(rootItem);
        buildFullItemTreeWithDuplicates(tree, rootItem.getId());
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
    public ItemTree getFullItemTree(Long itemId) {
        Optional<Item> itemOpt = itemRepository.findById(itemId);
        if (itemOpt.isEmpty()) {
            throw new IllegalArgumentException("Item not found");
        }

        Item rootItem = itemOpt.get();
        ItemTree tree = new ItemTree(rootItem);
        buildFullItemTreeWithDuplicates(tree, itemId);
        return tree;
    }

    private void buildFullItemTreeWithDuplicates(ItemTree tree, Long itemId) {
        List<ItemRecipe> recipes = itemRecipeRepository.findByParentItemId(itemId);

        System.out.println("Building tree for item " + itemId + ", found " + recipes.size() + " recipe entries");

        for (ItemRecipe recipe : recipes) {
            Optional<Item> componentOpt = itemRepository.findById(recipe.getComponentItemId());
            if (componentOpt.isPresent()) {
                Item component = componentOpt.get();

                for (int i = 0; i < recipe.getQuantity(); i++) {
                    ItemTree childTree = new ItemTree(component);

                    System.out.println("Adding component: " + component.getName() +
                            " (ID: " + component.getId() + ") x" + recipe.getQuantity() +
                            " to parent: " + itemId);

                    tree.addComponent(childTree);

                    buildFullItemTreeWithDuplicates(childTree, component.getId());
                }
            }
        }
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


    @Override
    public void saveRecipeTree(Long rootItemId, ItemTree tree) {
        clearItemRecipe(rootItemId);

        saveTreeRecursiveWithQuantity(rootItemId, tree);
    }

    private void saveTreeRecursiveWithQuantity(Long parentItemId, ItemTree tree) {
        if (tree.getComponents() != null && !tree.getComponents().isEmpty()) {
            Map<Long, Integer> componentQuantities = new HashMap<>();
            Map<Long, ItemTree> componentTrees = new HashMap<>();

            for (ItemTree componentTree : tree.getComponents()) {
                Long componentId = componentTree.getItem().getId();
                componentQuantities.put(componentId,
                        componentQuantities.getOrDefault(componentId, 0) + 1);
                componentTrees.putIfAbsent(componentId, componentTree);
            }

            for (Map.Entry<Long, Integer> entry : componentQuantities.entrySet()) {
                Long componentId = entry.getKey();
                Integer quantity = entry.getValue();

                ItemRecipe recipe = new ItemRecipe();
                recipe.setParentItemId(parentItemId);
                recipe.setComponentItemId(componentId);
                recipe.setQuantity(quantity);

                System.out.println("Saving recipe: parent=" + parentItemId +
                        ", component=" + componentId + ", quantity=" + quantity);

                itemRecipeRepository.save(recipe);

                ItemTree componentTree = componentTrees.get(componentId);
                if (componentTree.getComponents() != null && !componentTree.getComponents().isEmpty()) {
                    saveTreeRecursiveWithQuantity(componentId, componentTree);
                }
            }
        }
    }

    @Override
    public void clearItemRecipe(Long itemId) {
        itemRecipeRepository.deleteByParentItemId(itemId);
    }
}
