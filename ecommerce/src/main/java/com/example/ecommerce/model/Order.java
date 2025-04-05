package com.example.ecommerce.model;

import jakarta.persistence.*;
import java.math.BigDecimal; 
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;      

@Entity
@Table(name = "orders")  
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY) // Fetch user details only when needed
  @JoinColumn(name = "user_id", nullable = false)
  private User user;


    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private List<OrderItem> orderItems = new ArrayList<>(); // Initialize the list

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal totalPrice; 

  @Column(nullable = false) // Status should not be null
  private String status; // e.g., "PENDING_PAYMENT", "PROCESSING", etc.

  @Column(name = "created_at", nullable = false, updatable = false) 
  private LocalDateTime createdAt;

  // Automatically set creation timestamp
  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }

  // --- Getters and Setters ---

  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }
  public User getUser() {
    return user;
  }
  public void setUser(User user) {
    this.user = user;
  }

  // Use BigDecimal for getter/setter
  public BigDecimal getTotalPrice() {
    return totalPrice;
  }
  public void setTotalPrice(BigDecimal totalPrice) {
    this.totalPrice = totalPrice;
  }
  public String getStatus() {
    return status;
  }
  public void setStatus(String status) {
    this.status = status;
  }
  public LocalDateTime getCreatedAt() {
    return createdAt;
  }
  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  // **** ADDED GETTER AND SETTER for orderItems ****
  public List<OrderItem> getOrderItems() {
    return orderItems;
  }

  public void setOrderItems(List<OrderItem> orderItems) {
    this.orderItems = orderItems;
  }

  public void addOrderItem(OrderItem item) {
    this.orderItems.add(item);
    item.setOrder(this); // Maintain bidirectional link
  }

  public void removeOrderItem(OrderItem item) {
    this.orderItems.remove(item);
    item.setOrder(null);
  }

 

}
