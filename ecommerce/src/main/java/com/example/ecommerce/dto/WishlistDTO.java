package com.example.ecommerce.dto;

import java.util.List;

public class WishlistDTO {
    private Long id;
    private Long userId;
    private List<WishlistItemDTO> items;

    // Constructors
    public WishlistDTO() {}

    public WishlistDTO(Long id, Long userId, List<WishlistItemDTO> items) {
        this.id = id;
        this.userId = userId;
        this.items = items;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public List<WishlistItemDTO> getItems() { return items; }
    public void setItems(List<WishlistItemDTO> items) { this.items = items; }
}