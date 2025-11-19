package ru.itis.wr.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemTree {
    private Item item;
    private List<ItemTree> components;

    public ItemTree() {};

    public ItemTree(Item item) {
        this.item = item;
        this.components = new ArrayList<>();
    }

    public ItemTree(Item item, int depth) {
        this.item = item;
        this.components = new ArrayList<>();
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public List<ItemTree> getComponents() {
        return components;
    }

    public void setComponents(List<ItemTree> components) {
        this.components = components;
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
        int count = 1;
        for (ItemTree component : components) {
            count += component.getTotalComponents();
        }
        return count;
    }

    public List<Item> getAllItemsInTree() {
        List<Item> items = new ArrayList<>();
        items.add(item);

        for (ItemTree component : components) {
            items.addAll(component.getAllItemsInTree());
        }

        return items;
    }

    public Item findItemInTree(Long itemId) {
        if (item.getId().equals(itemId)) {
            return item;
        }

        for (ItemTree component : components) {
            Item found = component.findItemInTree(itemId);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return toStringWithIndent(0);
    }

    private String toStringWithIndent(int indent) {
        StringBuilder sb = new StringBuilder();
        String indentStr = "  ".repeat(indent);

        sb.append(indentStr).append("└── ").append(item.getName())
                .append(" (").append(item.getCost()).append("g)");

        for (ItemTree component : components) {
            sb.append("\n").append(component.toStringWithIndent(indent + 2));
        }

        return sb.toString();
    }

    public Object toJsonStructure() {
        return Map.of(
                "item", Map.of(
                        "id", item.getId(),
                        "name", item.getName(),
                        "cost", item.getCost(),
                        "rarity", item.getRarity().name(),
                        "iconUrl", item.getIconUrl()
                ),
                "components", components.stream()
                        .map(ItemTree::toJsonStructure)
                        .collect(java.util.stream.Collectors.toList()),
                "hasComponents", !components.isEmpty()
        );
    }
}
