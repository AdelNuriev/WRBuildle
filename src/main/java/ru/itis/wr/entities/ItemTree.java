package ru.itis.wr.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemTree {
    private Item rootItem;
    private List<ItemTree> components;
    private int depth;

    public ItemTree(Item rootItem) {
        this.rootItem = rootItem;
        this.components = new ArrayList<>();
        this.depth = 0;
    }

    public ItemTree(Item rootItem, int depth) {
        this.rootItem = rootItem;
        this.components = new ArrayList<>();
        this.depth = depth;
    }

    public Item getRootItem() {
        return rootItem;
    }

    public void setRootItem(Item rootItem) {
        this.rootItem = rootItem;
    }

    public List<ItemTree> getComponents() {
        return components;
    }

    public void setComponents(List<ItemTree> components) {
        this.components = components;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public void addComponent(ItemTree component) {
        this.components.add(component);
    }

    public boolean hasComponents() {
        return !components.isEmpty();
    }

    public boolean isLeaf() {
        return components.isEmpty();
    }

    public int getTotalComponents() {
        int count = 1; // Сам предмет
        for (ItemTree component : components) {
            count += component.getTotalComponents();
        }
        return count;
    }

    public List<Item> getAllItemsInTree() {
        List<Item> items = new ArrayList<>();
        items.add(rootItem);

        for (ItemTree component : components) {
            items.addAll(component.getAllItemsInTree());
        }

        return items;
    }

    public Item findItemInTree(Long itemId) {
        if (rootItem.getId() == itemId) {
            return rootItem;
        }

        for (ItemTree component : components) {
            Item found = component.findItemInTree(itemId);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    public int getMaxDepth() {
        int maxDepth = this.depth;
        for (ItemTree component : components) {
            maxDepth = Math.max(maxDepth, component.getMaxDepth());
        }
        return maxDepth;
    }

    @Override
    public String toString() {
        return toStringWithIndent(0);
    }

    private String toStringWithIndent(int indent) {
        StringBuilder sb = new StringBuilder();
        String indentStr = "  ".repeat(indent);

        sb.append(indentStr).append("└── ").append(rootItem.getName())
                .append(" (").append(rootItem.getCost()).append("g)");

        for (ItemTree component : components) {
            sb.append("\n").append(component.toStringWithIndent(indent + 2));
        }

        return sb.toString();
    }

    public Object toJsonStructure() {
        return Map.of(
                "item", Map.of(
                        "id", rootItem.getId(),
                        "name", rootItem.getName(),
                        "cost", rootItem.getCost(),
                        "rarity", rootItem.getRarity().name(),
                        "iconUrl", rootItem.getIconUrl()
                ),
                "components", components.stream()
                        .map(ItemTree::toJsonStructure)
                        .collect(java.util.stream.Collectors.toList()),
                "hasComponents", !components.isEmpty()
        );
    }
}
