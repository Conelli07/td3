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

        System.out.println("Liste de tous les plats");
        List<Dish> dishes = dataRetriever.findAllDishes();
        for (Dish d : dishes) {
            System.out.println(d);
        }

        System.out.println("\nListe de tous les ingredients");
        List<Ingredient> ingredients = dataRetriever.findAllIngredients();
        for (Ingredient ingredient : ingredients) {
            System.out.println(ingredient);
        }

        System.out.println("\nRelations entre plats et ingredients");
        List<DishIngredient> dishIngredients = dataRetriever.findAllDishIngredients();
        for (DishIngredient di : dishIngredients) {
            System.out.println(di);
        }

        System.out.println("\nTest des couts des plats");

        int[] dishIds = {1, 2, 3, 4, 5};
        double[] expectedCosts = {250.0, 4500.0, 0.0, 1400.0, 0.0};

        for (int i = 0; i < dishIds.length; i++) {
            Dish testDish = dataRetriever.findDishById(dishIds[i]);
            double cost = testDish.getDishCost();

            String status = (Math.abs(cost - expectedCosts[i]) < 0.01) ? "PASS" : "FAIL";
            System.out.printf("%s - %s (id=%d): %.2f Ar (attendu: %.2f Ar)%n",
                    status, testDish.getName(), dishIds[i], cost, expectedCosts[i]);
        }

        System.out.println("\nDetails des couts par plat");

        for (int dishId : dishIds) {
            Dish detailDish = dataRetriever.findDishById(dishId);
            System.out.println("\n" + detailDish.getName());
            System.out.println("----------------------------------------");

            if (detailDish.getIngredients() != null && !detailDish.getIngredients().isEmpty()) {
                for (Ingredient ing : detailDish.getIngredients()) {
                    double ingredientCost = ing.getPrice() * ing.getQuantity();
                    System.out.printf("  %s: %.3f %s x %.2f Ar = %.2f Ar%n",
                            ing.getName(),
                            ing.getQuantity(),
                            ing.getUnit(),
                            ing.getPrice(),
                            ingredientCost
                    );
                }
            } else {
                System.out.println("  Aucun ingredient");
            }

            double totalCost = detailDish.getDishCost();
            System.out.println("----------------------------------------");
            System.out.printf("Cout total: %.2f Ar%n", totalCost);

            if (detailDish.getPrice() != null) {
                System.out.printf("Prix de vente: %.2f Ar%n", detailDish.getPrice());
                try {
                    double margin = detailDish.getGrossMargin();
                    System.out.printf("Marge brute: %.2f Ar%n", margin);
                } catch (IllegalStateException e) {
                    System.out.println("Marge brute: N/A (prix NULL)");
                }
            }
        }

        System.out.println("\nTest des marges brutes");

        double[] expectedMargins = {3250.0, 7500.0, 0.0, 6600.0, 0.0};
        boolean[] shouldThrowException = {false, false, true, false, true};

        for (int i = 0; i < dishIds.length; i++) {
            Dish marginDish = dataRetriever.findDishById(dishIds[i]);

            try {
                double margin = marginDish.getGrossMargin();
                if (shouldThrowException[i]) {
                    System.out.println("FAIL - " + marginDish.getName() + ": devrait lever une exception");
                } else {
                    String status = (Math.abs(margin - expectedMargins[i]) < 0.01) ? "PASS" : "FAIL";
                    System.out.printf("%s - %s (id=%d): %.2f Ar (attendu: %.2f Ar)%n",
                            status, marginDish.getName(), dishIds[i], margin, expectedMargins[i]);
                }
            } catch (IllegalStateException e) {
                if (shouldThrowException[i]) {
                    System.out.println("PASS - " + marginDish.getName() + ": exception levee correctement");
                } else {
                    System.out.println("FAIL - " + marginDish.getName() + ": " + e.getMessage());
                }
            }
        }

        System.out.println("\nAjout de nouvelles donnees");

        Ingredient testIngredient = new Ingredient();
        testIngredient.setName("Sel");
        testIngredient.setPrice(200.0);
        testIngredient.setCategory(CategoryEnum.OTHER);

        Ingredient testSaved = dataRetriever.saveIngredient(testIngredient);
        System.out.println("Nouvel ingredient ajoute: " + testSaved.getName() + " (ID: " + testSaved.getId() + ")");

        DishIngredient testDI = new DishIngredient();
        testDI.setDishId(1);
        testDI.setIngredientId(testSaved.getId());
        testDI.setQuantity(0.01);
        testDI.setUnit("KG");

        DishIngredient testDISaved = dataRetriever.saveDishIngredient(testDI);
        System.out.println("Nouvelle relation creee (ID: " + testDISaved.getId() + ")");

        System.out.println("\nTests termines");
    }
}