public class Dishingredient {
    private int id;
    private int dishId;
    private int ingredientId;
    private double quantity;
    private String unit;

    public Dishingredient() {
    }

    public Dishingredient(int id, int dishId, int ingredientId, double quantity, String unit) {
        this.id = id;
        this.dishId = dishId;
        this.ingredientId = ingredientId;
        this.quantity = quantity;
        this.unit = unit;
    }

    public Dishingredient(int dishId, int ingredientId, double quantity, String unit) {
        this.dishId = dishId;
        this.ingredientId = ingredientId;
        this.quantity = quantity;
        this.unit = unit;
    }

    public int getId() {
        return id;
    }

    public int getDishId() {
        return dishId;
    }

    public int getIngredientId() {
        return ingredientId;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDishId(int dishId) {
        this.dishId = dishId;
    }

    public void setIngredientId(int ingredientId) {
        this.ingredientId = ingredientId;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return "DishIngredient{" +
                "id=" + id +
                ", dishId=" + dishId +
                ", ingredientId=" + ingredientId +
                ", quantity=" + quantity +
                ", unit='" + unit + '\'' +
                '}';
    }
}