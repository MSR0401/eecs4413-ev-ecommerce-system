package com.example.ecommerce.dto;

import java.time.LocalDateTime;

public class WishlistItemDTO {
    private Long id;
    private VehicleSummaryDTO vehicle; // 
    private LocalDateTime addedAt;

    // Constructors
    public WishlistItemDTO() {}

    public WishlistItemDTO(Long id, VehicleSummaryDTO vehicle, LocalDateTime addedAt) {
        this.id = id;
        this.vehicle = vehicle;
        this.addedAt = addedAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public VehicleSummaryDTO getVehicle() { return vehicle; }
    public void setVehicle(VehicleSummaryDTO vehicle) { this.vehicle = vehicle; }
    public LocalDateTime getAddedAt() { return addedAt; }
    public void setAddedAt(LocalDateTime addedAt) { this.addedAt = addedAt; }
}
