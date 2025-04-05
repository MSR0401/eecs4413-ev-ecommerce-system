package com.example.ecommerce.controller;

import com.example.ecommerce.model.Order;
import com.example.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // Endpoint to initiate checkout from the current user's cart
    @PostMapping("/checkout")
    public ResponseEntity<?> checkout() {
        try {
            Order createdOrder = orderService.checkout();
            return ResponseEntity.ok(createdOrder); // Return the created order details
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body("User not authenticated.");
        } catch (RuntimeException e) {
            // Handle specific cases like empty cart, stock issues, payment failure
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Log the exception e
            return ResponseEntity.status(500).body("An internal error occurred during checkout.");
        }
    }


}