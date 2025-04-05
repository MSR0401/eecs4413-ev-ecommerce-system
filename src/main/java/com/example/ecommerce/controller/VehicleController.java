package com.example.ecommerce.controller;

import com.example.ecommerce.model.Vehicle;
import com.example.ecommerce.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Comparator;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

  @Autowired
  private VehicleRepository vehicleRepository;

  // GET all vehicles with optional filtering and sorting
  @GetMapping
  public List<Vehicle> getVehicles(
          @RequestParam(required = false) String type,
          @RequestParam(required = false) String sortBy,
          @RequestParam(required = false) Boolean hotDeals,
          @RequestParam(required = false) String make, // Add make filter
          @RequestParam(required = false) String model, // Add model filter
          @RequestParam(required = false) Double minPrice, // Add minPrice filter
          @RequestParam(required = false) Double maxPrice  // Add maxPrice filter
  ) {

    List<Vehicle> vehicles = vehicleRepository.findAll();

    // Filtering
    if (type != null && !type.isEmpty()) {
      vehicles = vehicles.stream()
              .filter(v -> v.getType().equalsIgnoreCase(type))
              .collect(Collectors.toList());
    }
    if (make != null && !make.isEmpty()) {
      vehicles = vehicles.stream()
              .filter(v -> v.getMake().equalsIgnoreCase(make))
              .collect(Collectors.toList());
    }
    if (model != null && !model.isEmpty()) {
      vehicles = vehicles.stream()
              .filter(v -> v.getModel().equalsIgnoreCase(model))
              .collect(Collectors.toList());
    }

    // Price Range Filtering
    if (minPrice != null) {
      vehicles = vehicles.stream()
              .filter(v -> v.getPrice() >= minPrice)
              .collect(Collectors.toList());
    }
    if (maxPrice != null) {
      vehicles = vehicles.stream()
              .filter(v -> v.getPrice() <= maxPrice)
              .collect(Collectors.toList());
    }

    // Hot Deals Filter
    if (hotDeals != null && hotDeals) {
      vehicles = vehicles.stream()
              .filter(v -> v.getPrice() < 50000.00) // Example: Vehicles under $50,000 are "hot deals"
              .collect(Collectors.toList());
    }

    // Sorting
    if (sortBy != null && !sortBy.isEmpty()) {
      switch (sortBy.toLowerCase()) {
        case "price":
          vehicles.sort(Comparator.comparingDouble(Vehicle::getPrice));
          break;
        case "year":
          vehicles.sort(Comparator.comparingInt(Vehicle::getYear));
          break;
        // Add more sorting options as needed
      }
    }

    return vehicles;
  }

  // GET vehicle details by id
  @GetMapping("/{id}")
  public Vehicle getVehicleById(@PathVariable Long id) {
    return vehicleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Vehicle not found"));
  }
}