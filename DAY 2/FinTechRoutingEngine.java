interface PaymentStrategy {
    boolean processPayment(double amount);
}

class CreditCardStrategy implements PaymentStrategy {

    @Override
    public boolean processPayment(double amount) {
        System.out.println("Processing Credit Card payment of ₹" + amount);
        return true;
    }
}

class CryptoStrategy implements PaymentStrategy {

    @Override
    public boolean processPayment(double amount) {
        System.out.println("Processing Cryptocurrency payment of ₹" + amount);
        return true;
    }
}

class TransactionProcessor {

    private PaymentStrategy strategy;

    public TransactionProcessor(PaymentStrategy strategy) {
        this.strategy = strategy;
    }

    public void setPaymentStrategy(PaymentStrategy strategy) {
        this.strategy = strategy;
    }

    public void executeTransaction(double amount) {

        if (strategy == null) {
            System.out.println("No payment strategy selected.");
            return;
        }

        boolean status = strategy.processPayment(amount);

        if (status) {
            System.out.println("Transaction Successful");
        } else {
            System.out.println("Transaction Failed");
        }
    }
}

public class FinTechRoutingEngine {

    public static void main(String[] args) {

        TransactionProcessor processor =
                new TransactionProcessor(
                        new CreditCardStrategy());

        processor.executeTransaction(5000);

        System.out.println();

        processor.setPaymentStrategy(
                new CryptoStrategy());

        processor.executeTransaction(5000);
    }
}