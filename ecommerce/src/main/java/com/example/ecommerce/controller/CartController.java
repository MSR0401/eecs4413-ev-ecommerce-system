package com.example.ecommerce.controller;

import com.example.ecommerce.dto.CartDTO; // Import DTOs
import com.example.ecommerce.dto.CartItemDTO;
import com.example.ecommerce.dto.VehicleSummaryDTO;
import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Vehicle;
import com.example.ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    // Get current user's cart as DTO
    @GetMapping
    public ResponseEntity<?> getCurrentUserCart() {
        try {
            Cart cartEntity = cartService.getCartForCurrentUser();
            CartDTO cartDTO = convertToDTO(cartEntity); // Convert entity to DTO
            return ResponseEntity.ok(cartDTO); // Return the DTO
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body("{\"error\": \"User not authenticated\"}");
        } catch (Exception e) {
            System.err.println("Error fetching cart: " + e.getMessage()); // Basic logging
            e.printStackTrace(); // Print stack trace for debugging
            return ResponseEntity.status(500).body("{\"error\": \"Internal server error fetching cart\"}");
        }
    }

    // Add item - Returns updated CartDTO
    @PostMapping("/items")
    public ResponseEntity<?> addItemToCart(@RequestBody AddToCartRequest request) {
        try {
            // Service handles adding the item
            cartService.addItemToCart(request.getVehicleId(), request.getQuantity());

            // Fetch the whole cart again to get the updated state
            Cart updatedCartEntity = cartService.getCartForCurrentUser();
            CartDTO updatedCartDTO = convertToDTO(updatedCartEntity);

            return ResponseEntity.ok(updatedCartDTO);
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body("{\"error\": \"User not authenticated\"}");
        } catch (RuntimeException e) {
            System.err.println("Error adding item to cart: " + e.getMessage());
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            System.err.println("Unexpected error adding item to cart: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("{\"error\": \"Error adding item to cart\"}");
        }
    }


    // Remove item - Returns updated CartDTO
    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<?> removeItemFromCart(@PathVariable Long cartItemId) {
        try {
            cartService.removeItemFromCart(cartItemId);
            // Fetch and return the updated cart
            Cart updatedCartEntity = cartService.getCartForCurrentUser();
            CartDTO updatedCartDTO = convertToDTO(updatedCartEntity);
            return ResponseEntity.ok(updatedCartDTO); // Return updated cart
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body("{\"error\": \"User not authenticated\"}");
        } catch (RuntimeException e) { // Catch specific exceptions like not found
            System.err.println("Error removing item from cart: " + e.getMessage());
            return ResponseEntity.status(404).body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            System.err.println("Unexpected error removing item from cart: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("{\"error\": \"Error removing item from cart\"}");
        }
    }

    // --- DTO Conversion Helper Method (within CartController) ---
    private CartDTO convertToDTO(Cart cart) {
        if (cart == null || cart.getUser() == null) {

            System.err.println("Warning: Attempted to convert a null cart or cart with null user.");
            return new CartDTO(null, null, List.of());
        }

        List<CartItemDTO> itemDTOs = cart.getItems().stream()
                .map(this::convertItemToDTO) // Reference the helper method below
                .collect(Collectors.toList());

        // Pass user ID from the Cart's User object
        return new CartDTO(cart.getId(), cart.getUser().getId(), itemDTOs);
    }

    // --- DTO Conversion Helper Method for Items (within CartController) ---
    private CartItemDTO convertItemToDTO(CartItem item) {
        Vehicle vehicle = item.getVehicle();
        // Handle cases where associated vehicle might be null
        VehicleSummaryDTO vehicleDTO = null;
        if (vehicle != null) {
            vehicleDTO = new VehicleSummaryDTO(
                    vehicle.getId(),
                    vehicle.getMake(),
                    vehicle.getModel(),
                    vehicle.getYear(),
                    vehicle.getPrice(),
                    vehicle.getImageUrl()
            );
        } else {
            // Log this issue
            System.err.println("Warning: CartItem ID " + item.getId() + " references a null Vehicle.");
            // Create a placeholder DTO or handle as needed
            vehicleDTO = new VehicleSummaryDTO(null, "Unknown", "Unknown", 0, 0.0, null);
        }
        return new CartItemDTO(item.getId(), item.getQuantity(), vehicleDTO);
    }


    // Static inner class for request body
    static class AddToCartRequest {
        private Long vehicleId;
        private int quantity;

        public Long getVehicleId() { return vehicleId; }
        public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }


}
