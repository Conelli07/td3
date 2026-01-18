import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {

    Dish findDishById(Integer id) {
        String sql = """
                select dish.id as dish_id, dish.name as dish_name, dish_type, dish.price as dish_price
                from dish
                where dish.id = ?;
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Dish dish = new Dish();
                    dish.setId(resultSet.getInt("dish_id"));
                    dish.setName(resultSet.getString("dish_name"));
                    dish.setDishType(DishTypeEnum.valueOf(resultSet.getString("dish_type").toUpperCase()));
                    dish.setPrice(resultSet.getObject("dish_price") == null
                            ? null : resultSet.getDouble("dish_price"));
                    dish.setIngredients(findIngredientsByDishId(id));
                    return dish;
                }
            }

            throw new RuntimeException("Dish not found " + id);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    List<Dish> findAllDishes() {
        String sql = """
                select dish.id as dish_id, dish.name as dish_name, dish_type, dish.price as dish_price
                from dish
                order by dish.id;
                """;

        List<Dish> dishes = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Dish dish = new Dish();
                dish.setId(resultSet.getInt("dish_id"));
                dish.setName(resultSet.getString("dish_name"));
                dish.setDishType(DishTypeEnum.valueOf(resultSet.getString("dish_type").toUpperCase()));
                dish.setPrice(resultSet.getObject("dish_price") == null
                        ? null : resultSet.getDouble("dish_price"));
                dish.setIngredients(findIngredientsByDishId(dish.getId()));
                dishes.add(dish);
            }

            return dishes;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Ingredient> findIngredientsByDishId(Integer dishId) {
        String sql = """
                select i.id, i.name, i.price, i.category, di.quantity, di.unit
                from ingredient i
                join dishingredient di on i.id = di.ingredient_id
                where di.dish_id = ?;
                """;

        List<Ingredient> ingredients = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, dishId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Ingredient ingredient = new Ingredient();
                    ingredient.setId(rs.getInt("id"));
                    ingredient.setName(rs.getString("name"));
                    ingredient.setPrice(rs.getDouble("price"));
                    ingredient.setCategory(CategoryEnum.valueOf(rs.getString("category").toUpperCase()));
                    ingredient.setQuantity(rs.getDouble("quantity"));
                    ingredient.setUnit(rs.getString("unit"));
                    ingredients.add(ingredient);
                }
            }

            return ingredients;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    List<Ingredient> findAllIngredients() {
        String sql = """
                select id, name, price, category
                from ingredient
                order by id;
                """;

        List<Ingredient> ingredients = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Ingredient ingredient = new Ingredient();
                ingredient.setId(rs.getInt("id"));
                ingredient.setName(rs.getString("name"));
                ingredient.setPrice(rs.getDouble("price"));
                ingredient.setCategory(CategoryEnum.valueOf(rs.getString("category").toUpperCase()));
                ingredients.add(ingredient);
            }

            return ingredients;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    List<DishIngredient> findAllDishIngredients() {
        String sql = """
                select id, dish_id, ingredient_id, quantity, unit
                from dishingredient
                order by id;
                """;

        List<DishIngredient> dishIngredients = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                DishIngredient di = new DishIngredient();
                di.setId(rs.getInt("id"));
                di.setDishId(rs.getInt("dish_id"));
                di.setIngredientId(rs.getInt("ingredient_id"));
                di.setQuantity(rs.getDouble("quantity"));
                di.setUnit(rs.getString("unit"));
                dishIngredients.add(di);
            }

            return dishIngredients;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    Dish saveDish(Dish toSave) {
        String upsertDishSql = """
                INSERT INTO dish (id, price, name, dish_type)
                VALUES (?, ?, ?, ?::dish_type)
                ON CONFLICT (id) DO UPDATE
                SET name = EXCLUDED.name,
                    dish_type = EXCLUDED.dish_type,
                    price = EXCLUDED.price
                RETURNING id
                """;

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            Integer dishId;
            try (PreparedStatement ps = conn.prepareStatement(upsertDishSql)) {

                ps.setInt(1, toSave.getId() != null
                        ? toSave.getId()
                        : getNextSerialValue(conn, "dish", "id"));

                if (toSave.getPrice() != null) {
                    ps.setDouble(2, toSave.getPrice());
                } else {
                    ps.setNull(2, Types.DOUBLE);
                }

                ps.setString(3, toSave.getName());
                ps.setString(4, toSave.getDishType().name());

                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    dishId = rs.getInt(1);
                }
            }

            deleteDishIngredients(conn, dishId);

            if (toSave.getIngredients() != null && !toSave.getIngredients().isEmpty()) {
                saveDishIngredients(conn, dishId, toSave.getIngredients());
            }

            conn.commit();
            return findDishById(dishId);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    Ingredient saveIngredient(Ingredient ingredient) {
        String sql = """
                INSERT INTO ingredient (name, price, category)
                VALUES (?, ?, ?::ingredient_category)
                RETURNING id
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ingredient.getName());
            ps.setDouble(2, ingredient.getPrice());
            ps.setString(3, ingredient.getCategory().name());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ingredient.setId(rs.getInt(1));
                    return ingredient;
                }
            }

            throw new RuntimeException("Failed to save ingredient");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    DishIngredient saveDishIngredient(DishIngredient dishIngredient) {
        String sql = """
                INSERT INTO dishingredient (dish_id, ingredient_id, quantity, unit)
                VALUES (?, ?, ?, ?::unit_type)
                RETURNING id
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, dishIngredient.getDishId());
            ps.setInt(2, dishIngredient.getIngredientId());
            ps.setDouble(3, dishIngredient.getQuantity());
            ps.setString(4, dishIngredient.getUnit());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    dishIngredient.setId(rs.getInt(1));
                    return dishIngredient;
                }
            }

            throw new RuntimeException("Failed to save DishIngredient");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteDishIngredients(Connection conn, Integer dishId) throws SQLException {
        String sql = "DELETE FROM dishingredient WHERE dish_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dishId);
            ps.executeUpdate();
        }
    }

    private void saveDishIngredients(Connection conn, Integer dishId, List<Ingredient> ingredients)
            throws SQLException {

        String sql = """
                INSERT INTO dishingredient (dish_id, ingredient_id, quantity, unit)
                VALUES (?, ?, ?, ?::unit_type)
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Ingredient ingredient : ingredients) {
                ps.setInt(1, dishId);
                ps.setInt(2, ingredient.getId());
                ps.setDouble(3, ingredient.getQuantity() != null ? ingredient.getQuantity() : 0.0);
                ps.setString(4, ingredient.getUnit() != null ? ingredient.getUnit() : "PIECE");
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private String getSerialSequenceName(Connection conn, String tableName, String columnName)
            throws SQLException {

        try (PreparedStatement ps = conn.prepareStatement("SELECT pg_get_serial_sequence(?, ?)")) {
            ps.setString(1, tableName);
            ps.setString(2, columnName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString(1);
            }
        }
        return null;
    }

    private int getNextSerialValue(Connection conn, String tableName, String columnName)
            throws SQLException {

        String sequenceName = getSerialSequenceName(conn, tableName, columnName);
        if (sequenceName == null) {
            throw new IllegalArgumentException("No sequence for " + tableName + "." + columnName);
        }

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT setval(?, (SELECT COALESCE(MAX(" + columnName + "), 0) FROM " + tableName + "))")) {
            ps.setString(1, sequenceName);
            ps.executeQuery();
        }

        try (PreparedStatement ps = conn.prepareStatement("SELECT nextval(?)")) {
            ps.setString(1, sequenceName);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }
}