import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

class Order {

    private String orderId;
    private double amount;

    public Order(String orderId, double amount) {
        this.orderId = orderId;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Order{id='" + orderId +
                "', amount=" + amount + "}";
    }
}

class ExchangeManager {

    private final ConcurrentHashMap<String,
            CopyOnWriteArrayList<Order>> orderBook =
            new ConcurrentHashMap<>();

    public void placeOrder(String ticker, Order order) {

        orderBook
                .computeIfAbsent(
                        ticker,
                        k -> new CopyOnWriteArrayList<>()
                )
                .add(order);
    }

    public List<Order> getOrders(String ticker) {
        return orderBook.getOrDefault(
                ticker,
                new CopyOnWriteArrayList<>()
        );
    }
}

public class TradeXOrderMatchingEngine {

    public static void main(String[] args) {

        ExchangeManager exchange = new ExchangeManager();

        exchange.placeOrder(
                "BTC",
                new Order("ORD001", 50000)
        );

        exchange.placeOrder(
                "BTC",
                new Order("ORD002", 51000)
        );

        exchange.placeOrder(
                "ETH",
                new Order("ORD003", 3000)
        );

        System.out.println("BTC Orders:");
        System.out.println(exchange.getOrders("BTC"));

        System.out.println("ETH Orders:");
        System.out.println(exchange.getOrders("ETH"));
    }
}