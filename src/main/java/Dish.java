import java.util.List;
import java.util.Objects;

public class Dish {
    private Integer id;
    private Double price;
    private String name;
    private DishTypeEnum dishType;
    private List<Ingredient> ingredients;

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getDishCost() {
        if (ingredients == null || ingredients.isEmpty()) {
            return 0.0;
        }
        double totalPrice = 0;
        for (Ingredient ingredient : ingredients) {
            Double quantity = ingredient.getQuantity();
            if (quantity == null) {
                throw new RuntimeException("Quantity is null for ingredient: " + ingredient.getName());
            }
            totalPrice += ingredient.getPrice() * quantity;
        }
        return totalPrice;
    }

    public Double getGrossMargin() {
        if (price == null) {
            throw new IllegalStateException("Le plat '" + name + "' n'a pas de prix de vente defini. Impossible de calculer la marge brute.");
        }
        return price - getDishCost();
    }

    public Dish() {
    }

    public Dish(Integer id, String name, DishTypeEnum dishType, Double price) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
        this.price = price;
    }

    public Dish(Integer id, String name, DishTypeEnum dishType, List<Ingredient> ingredients) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
        this.ingredients = ingredients;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DishTypeEnum getDishType() {
        return dishType;
    }

    public void setDishType(DishTypeEnum dishType) {
        this.dishType = dishType;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Dish dish = (Dish) o;
        return Objects.equals(id, dish.id) && Objects.equals(name, dish.name) && Objects.equals(dishType, dish.dishType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, dishType);
    }

    @Override
    public String toString() {
        return "Dish{" +
                "id=" + id +
                ", price=" + price +
                ", name='" + name + '\'' +
                ", dishType='" + dishType + '\'' +
                ", ingredients=" + (ingredients != null ? ingredients.size() + " items" : "null") +
                '}';
    }
}