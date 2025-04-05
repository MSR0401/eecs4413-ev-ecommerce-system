package com.example.ecommerce.dto;

import java.math.BigDecimal; 

public class CartItemDTO {
    private Long id;
    private int quantity;
    private VehicleSummaryDTO vehicle; 
    private BigDecimal subtotal; // Calculate and store subtotal

    // Constructors
    public CartItemDTO() {}

    public CartItemDTO(Long id, int quantity, VehicleSummaryDTO vehicle) {
        this.id = id;
        this.quantity = quantity;
        this.vehicle = vehicle;
        // Calculate subtotal upon creation
        if (vehicle != null && quantity > 0) {
            this.subtotal = BigDecimal.valueOf(vehicle.getPrice()).multiply(BigDecimal.valueOf(quantity));
        } else {
            this.subtotal = BigDecimal.ZERO;
        }
    }

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public VehicleSummaryDTO getVehicle() { return vehicle; }
    public void setVehicle(VehicleSummaryDTO vehicle) { this.vehicle = vehicle; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; } // Setter might not be needed if always calculated
}
