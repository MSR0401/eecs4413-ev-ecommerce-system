package com.example.ecommerce.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicles")
public class Vehicle {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  private String make;
  private String model;
  
  @Column(name = "model_year")
  private int year;
  
  private double price;
  private double batteryCapacity;
  private int rangeKm;
  private String type;          
  private int availableStock;
  private String imageUrl;

  @Column(name = "created_at")
  private LocalDateTime createdAt; 

  // Getters and Setters
  
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }
  public String getMake() {
    return make;
  }
  public void setMake(String make) {
    this.make = make;
  }
  public String getModel() {
    return model;
  }
  public void setModel(String model) {
    this.model = model;
  }
  public int getYear() {
    return year;
  }
  public void setYear(int modelYear) {
    this.year = modelYear;
  }
  public double getPrice() {
    return price;
  }
  public void setPrice(double price) {
    this.price = price;
  }
  public double getBatteryCapacity() {
    return batteryCapacity;
  }
  public void setBatteryCapacity(double batteryCapacity) {
    this.batteryCapacity = batteryCapacity;
  }
  public int getRangeKm() {
    return rangeKm;
  }
  public void setRangeKm(int rangeKm) {
    this.rangeKm = rangeKm;
  }
  public String getType() {
    return type;
  }
  public void setType(String type) {
    this.type = type;
  }
  public int getAvailableStock() {
    return availableStock;
  }
  public void setAvailableStock(int availableStock) {
    this.availableStock = availableStock;
  }
  public String getImageUrl() {
    return imageUrl;
  }
  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }
  
  public LocalDateTime getCreatedAt() {
    return createdAt;
  }
  
  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
