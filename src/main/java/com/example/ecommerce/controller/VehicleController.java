package com.example.ecommerce.controller;

import com.example.ecommerce.model.Vehicle;
import com.example.ecommerce.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

  @Autowired
  private VehicleRepository vehicleRepository;

  // GET all vehicles
  @GetMapping
  public List<Vehicle> getAllVehicles() {
    return vehicleRepository.findAll();
  }

  // GET vehicle details by id
  @GetMapping("/{id}")
  public Vehicle getVehicleById(@PathVariable Long id) {
    return vehicleRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("Vehicle not found"));
  }
}
