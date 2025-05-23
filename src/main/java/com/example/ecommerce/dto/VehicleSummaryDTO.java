package com.example.ecommerce.dto;

public class VehicleSummaryDTO {
    private Long id;
    private String make;
    private String model;
    private int year;
    private double price; 
    private String imageUrl;

    public VehicleSummaryDTO() {}

    public VehicleSummaryDTO(Long id, String make, String model, int year, double price, String imageUrl) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.year = year;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
