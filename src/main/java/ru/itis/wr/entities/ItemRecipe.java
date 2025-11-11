package ru.itis.wr.entities;

public class ItemRecipe {
    private Long id;
    private Long parentItemId;
    private Long componentItemId;
    private Integer quantity;

    public ItemRecipe() {}

    public ItemRecipe(Long id, Long parentItemId, Long componentItemId, Integer quantity) {
        this.id = id;
        this.parentItemId = parentItemId;
        this.componentItemId = componentItemId;
        this.quantity = quantity;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getParentItemId() { return parentItemId; }
    public void setParentItemId(Long parentItemId) { this.parentItemId = parentItemId; }
    public Long getComponentItemId() { return componentItemId; }
    public void setComponentItemId(Long componentItemId) { this.componentItemId = componentItemId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
