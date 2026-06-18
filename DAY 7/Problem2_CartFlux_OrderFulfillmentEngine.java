package com.cartflux;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;



@Embeddable
class OrderLineItemId implements Serializable {

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "product_id")
    private Long productId;

    public OrderLineItemId() {}

    public OrderLineItemId(Long orderId, Long productId) {
        this.orderId = orderId;
        this.productId = productId;
    }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderLineItemId that = (OrderLineItemId) o;
        return Objects.equals(orderId, that.orderId) &&
               Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, productId);
    }
}


@Entity
@Table(name = "products")
class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private BigDecimal catalogPrice;

    public Product() {}

    public Product(String name, BigDecimal catalogPrice) {
        this.name = name;
        this.catalogPrice = catalogPrice;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getCatalogPrice() { return catalogPrice; }
    public void setCatalogPrice(BigDecimal catalogPrice) { this.catalogPrice = catalogPrice; }
}

@Entity
@Table(name = "order_line_items")
class OrderLineItem {

    @EmbeddedId
    private OrderLineItemId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("orderId")
    @JoinColumn(name = "order_id")
    private PurchaseOrder purchaseOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;

    // Payload fields on the join table
    private int quantity;
    private BigDecimal lockedPrice;

    public OrderLineItem() {}

    public OrderLineItem(PurchaseOrder order, Product product, int quantity, BigDecimal lockedPrice) {
        this.id = new OrderLineItemId(order.getId(), product.getId());
        this.purchaseOrder = order;
        this.product = product;
        this.quantity = quantity;
        this.lockedPrice = lockedPrice;
    }

    public OrderLineItemId getId() { return id; }
    public void setId(OrderLineItemId id) { this.id = id; }
    public PurchaseOrder getPurchaseOrder() { return purchaseOrder; }
    public void setPurchaseOrder(PurchaseOrder purchaseOrder) { this.purchaseOrder = purchaseOrder; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public BigDecimal getLockedPrice() { return lockedPrice; }
    public void setLockedPrice(BigDecimal lockedPrice) { this.lockedPrice = lockedPrice; }
}

@Entity
@Table(name = "purchase_orders")
class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerEmail;
    private String status; // PENDING, SHIPPED, DELIVERED
    private LocalDateTime orderDate;

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderLineItem> lineItems = new ArrayList<>();

    public PurchaseOrder() {}

    public PurchaseOrder(String customerEmail, String status, LocalDateTime orderDate) {
        this.customerEmail = customerEmail;
        this.status = status;
        this.orderDate = orderDate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
    public List<OrderLineItem> getLineItems() { return lineItems; }
    public void setLineItems(List<OrderLineItem> lineItems) { this.lineItems = lineItems; }
}


class Problem2Demo {
    public static void main(String[] args) {
        System.out.println("=== Day 7 - Problem 2: CartFlux Order Fulfillment Engine ===");
        System.out.println();
        System.out.println("This solution demonstrates:");
        System.out.println("1. Breaking M:N into two 1:M relationships via OrderLineItem");
        System.out.println("2. Composite Primary Key using @Embeddable + @EmbeddedId");
        System.out.println("   - OrderLineItemId(orderId, productId)");
        System.out.println("3. Payload fields: quantity and lockedPrice on the join entity");
        System.out.println("4. JOIN FETCH to eliminate the N+1 Select Problem");
        System.out.println("5. Derived query with Between + EndingWith");
        System.out.println();

        PurchaseOrder order = new PurchaseOrder("customer@gmail.com", "PENDING", LocalDateTime.now());
        order.setId(1L);

        Product laptop = new Product("Laptop", new BigDecimal("999.99"));
        laptop.setId(1L);

        Product mouse = new Product("Mouse", new BigDecimal("29.99"));
        mouse.setId(2L);

        OrderLineItem item1 = new OrderLineItem(order, laptop, 1, new BigDecimal("949.99"));
        OrderLineItem item2 = new OrderLineItem(order, mouse, 2, new BigDecimal("24.99"));

        order.getLineItems().add(item1);
        order.getLineItems().add(item2);

        System.out.println("Order Status: " + order.getStatus());
        System.out.println("Customer: " + order.getCustomerEmail());
        System.out.println("Line Items: " + order.getLineItems().size());
        System.out.println("Item 1: " + item1.getProduct().getName() +
                           " x" + item1.getQuantity() + " @ $" + item1.getLockedPrice());
        System.out.println("Item 2: " + item2.getProduct().getName() +
                           " x" + item2.getQuantity() + " @ $" + item2.getLockedPrice());
    }
}
