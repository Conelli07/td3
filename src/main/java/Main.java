import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class Main {
    public static void main(String[] args) {
        try {
            System.setOut(new java.io.PrintStream(System.out, true, StandardCharsets.UTF_8));
            System.setErr(new java.io.PrintStream(System.err, true, StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }

        DataRetriever dataRetriever = new DataRetriever();

        System.out.println("Test de getStockValueAt(2024-01-06 12:00)");
        System.out.println("==========================================");

        Instant testTime = Instant.parse("2024-01-06T12:00:00Z");

        int[] ingredientIds = {1, 2, 3, 4, 5};
        String[] ingredientNames = {"Laitue", "Tomate", "Poulet", "Chocolat", "Beurre"};
        double[] expectedStocks = {4.8, 3.85, 9.0, 2.7, 2.3};

        for (int i = 0; i < ingredientIds.length; i++) {
            Ingredient ingredient = dataRetriever.findIngredientById(ingredientIds[i]);
            StockValue stockValue = ingredient.getStockValueAt(testTime);

            String status = (Math.abs(stockValue.getQuantity() - expectedStocks[i]) < 0.01) ? "PASS" : "FAIL";

            System.out.printf("%s - %s: %.2f %s (attendu: %.2f)%n",
                    status,
                    ingredientNames[i],
                    stockValue.getQuantity(),
                    stockValue.getUnit(),
                    expectedStocks[i]);
        }

        System.out.println("\nDetails des mouvements de stock");
        System.out.println("================================");

        for (int i = 0; i < ingredientIds.length; i++) {
            Ingredient ingredient = dataRetriever.findIngredientById(ingredientIds[i]);

            System.out.println("\n" + ingredientNames[i] + ":");
            System.out.println("----------------------------------------");

            if (ingredient.getStockMovementList() != null) {
                for (StockMovement movement : ingredient.getStockMovementList()) {
                    System.out.printf("  %s - %s %.3f %s (id=%d)%n",
                            movement.getCreationDatetime(),
                            movement.getType(),
                            movement.getValue().getQuantity(),
                            movement.getValue().getUnit(),
                            movement.getId());
                }
            }

            StockValue finalStock = ingredient.getStockValueAt(testTime);
            System.out.println("----------------------------------------");
            System.out.printf("Stock au %s: %.2f %s%n", testTime, finalStock.getQuantity(), finalStock.getUnit());
        }

        System.out.println("\nTests termines");
    }
}