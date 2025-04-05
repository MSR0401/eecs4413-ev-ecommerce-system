package com.example.ecommerce.model;

import jakarta.persistence.*;
import java.math.BigDecimal; 

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(nullable = false)
    private int quantity;

    // Store the price per item at the time of order
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerItem;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal; // quantity * pricePerItem

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public BigDecimal getPricePerItem() { return pricePerItem; }
    public void setPricePerItem(BigDecimal pricePerItem) { this.pricePerItem = pricePerItem; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    @PrePersist
    @PreUpdate
    private void calculateSubtotal() {
        if (pricePerItem != null && quantity > 0) {
            this.subtotal = pricePerItem.multiply(BigDecimal.valueOf(quantity));
        } else {
            this.subtotal = BigDecimal.ZERO;
        }
    }
}
