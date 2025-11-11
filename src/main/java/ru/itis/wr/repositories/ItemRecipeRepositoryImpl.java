package ru.itis.wr.repositories;

import ru.itis.wr.entities.ItemRecipe;
import ru.itis.wr.repositories.dataSource.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemRecipeRepositoryImpl implements ItemRecipeRepository {

    private final DatabaseConnection databaseConnection;

    private static final String SAVE_QUERY = """
        INSERT INTO item_recipes (parent_item_id, component_item_id, quantity) 
        VALUES (?, ?, ?) 
        RETURNING id
        """;

    private static final String FIND_BY_PARENT_QUERY = """
        SELECT ir.*, i.name as component_name 
        FROM item_recipes ir 
        JOIN items i ON ir.component_item_id = i.id 
        WHERE ir.parent_item_id = ?
        """;

    private static final String FIND_BY_COMPONENT_QUERY = """
        SELECT ir.*, i.name as parent_name 
        FROM item_recipes ir 
        JOIN items i ON ir.parent_item_id = i.id 
        WHERE ir.component_item_id = ?
        """;

    private static final String DELETE_BY_PARENT_QUERY = "DELETE FROM item_recipes WHERE parent_item_id = ?";
    private static final String EXISTS_QUERY = """
        SELECT COUNT(*) FROM item_recipes 
        WHERE parent_item_id = ? AND component_item_id = ?
        """;

    public ItemRecipeRepositoryImpl(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public void save(ItemRecipe recipe) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SAVE_QUERY)) {

            statement.setLong(1, recipe.getParentItemId());
            statement.setLong(2, recipe.getComponentItemId());
            statement.setInt(3, recipe.getQuantity());

            statement.executeQuery();

        } catch (SQLException e) {
            throw new RuntimeException("Error saving item recipe", e);
        }
    }

    @Override
    public List<ItemRecipe> findByParentItemId(Long parentItemId) {
        List<ItemRecipe> recipes = new ArrayList<>();
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_PARENT_QUERY)) {

            statement.setLong(1, parentItemId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                recipes.add(new ItemRecipe(
                        resultSet.getLong("id"),
                        resultSet.getLong("parent_item_id"),
                        resultSet.getLong("component_item_id"),
                        resultSet.getInt("quantity")
                ));
            }
            return recipes;

        } catch (SQLException e) {
            throw new RuntimeException("Error finding recipes by parent item", e);
        }
    }

    @Override
    public List<ItemRecipe> findByComponentItemId(Long componentItemId) {
        List<ItemRecipe> recipes = new ArrayList<>();
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_COMPONENT_QUERY)) {

            statement.setLong(1, componentItemId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                recipes.add(new ItemRecipe(
                        resultSet.getLong("id"),
                        resultSet.getLong("parent_item_id"),
                        resultSet.getLong("component_item_id"),
                        resultSet.getInt("quantity")
                ));
            }
            return recipes;

        } catch (SQLException e) {
            throw new RuntimeException("Error finding recipes by component item", e);
        }
    }

    @Override
    public void deleteByParentItemId(Long parentItemId) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_BY_PARENT_QUERY)) {

            statement.setLong(1, parentItemId);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting recipes by parent item", e);
        }
    }

    @Override
    public boolean existsByParentAndComponent(Long parentItemId, Long componentItemId) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(EXISTS_QUERY)) {

            statement.setLong(1, parentItemId);
            statement.setLong(2, componentItemId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
            return false;

        } catch (SQLException e) {
            throw new RuntimeException("Error checking recipe existence", e);
        }
    }
}
