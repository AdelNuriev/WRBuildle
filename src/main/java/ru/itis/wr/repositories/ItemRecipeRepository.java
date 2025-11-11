package ru.itis.wr.repositories;

import ru.itis.wr.entities.ItemRecipe;

import java.util.List;

public interface ItemRecipeRepository {
    void save(ItemRecipe recipe);
    List<ItemRecipe> findByParentItemId(Long parentItemId);
    List<ItemRecipe> findByComponentItemId(Long componentItemId);
    void deleteByParentItemId(Long parentItemId);
    boolean existsByParentAndComponent(Long parentItemId, Long componentItemId);
}
