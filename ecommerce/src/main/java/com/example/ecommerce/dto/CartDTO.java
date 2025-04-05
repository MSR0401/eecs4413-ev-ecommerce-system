package com.example.ecommerce.dto;

import java.math.BigDecimal;
import java.util.List;

public class CartDTO {
    private Long id;
    private Long userId; // Include user ID for reference
    private List<CartItemDTO> items;
    private BigDecimal totalPrice; // Calculate and store total

    // Constructors
    public CartDTO() {}

    public CartDTO(Long id, Long userId, List<CartItemDTO> items) {
        this.id = id;
        this.userId = userId;
        this.items = items;
        // Calculate total price upon creation
        if (items != null) {
            this.totalPrice = items.stream()
                    .map(CartItemDTO::getSubtotal) 
                    .reduce(BigDecimal.ZERO, BigDecimal::add); // Sum them up
        } else {
            this.totalPrice = BigDecimal.ZERO;
        }
    }

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public List<CartItemDTO> getItems() { return items; }
    public void setItems(List<CartItemDTO> items) { this.items = items; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; } // Setter might not be needed
}
