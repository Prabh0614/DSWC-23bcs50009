import java.util.Arrays;
import java.util.List;

class Transaction {

    private String status;
    private String category;
    private double amount;

    public Transaction(String status, String category, double amount) {
        this.status = status;
        this.category = category;
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }
}

class SalesAnalyzer {

    public double calculateElectronicsRevenue(
            List<Transaction> transactions) {

        return transactions.stream()
                .filter(t -> "COMPLETED".equals(t.getStatus()))
                .filter(t -> "ELECTRONICS".equals(t.getCategory()))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }
}

public class Main {

    public static void main(String[] args) {

        List<Transaction> transactions = Arrays.asList(
                new Transaction(
                        "COMPLETED",
                        "ELECTRONICS",
                        15000
                ),
                new Transaction(
                        "PENDING",
                        "ELECTRONICS",
                        7000
                ),
                new Transaction(
                        "COMPLETED",
                        "GROCERY",
                        2000
                ),
                new Transaction(
                        "COMPLETED",
                        "ELECTRONICS",
                        5000
                )
        );

        SalesAnalyzer analyzer = new SalesAnalyzer();

        double revenue =
                analyzer.calculateElectronicsRevenue(
                        transactions
                );

        System.out.println(
                "Electronics Revenue = " + revenue
        );
    }
}
