import java.nio.charset.StandardCharsets;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            System.setOut(new java.io.PrintStream(System.out, true, StandardCharsets.UTF_8));
            System.setErr(new java.io.PrintStream(System.err, true, StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }

        DataRetriever dataRetriever = new DataRetriever();

        Dish dish = dataRetriever.findDishById(4);
        System.out.println(dish);

        dish.setIngredients(List.of(new Ingredient(1), new Ingredient(2)));
        Dish newDish = dataRetriever.saveDish(dish);
        System.out.println(newDish);

        List<Ingredient> createdIngredients = dataRetriever.createIngredients(
                List.of(new Ingredient(null, "Fromage", CategoryEnum.DAIRY, 1200.0))
        );
        System.out.println(createdIngredients);
    }
}
