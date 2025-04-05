package com.example.ecommerce.controller;

import com.example.ecommerce.dto.WishlistDTO;
import com.example.ecommerce.dto.WishlistItemDTO;
import com.example.ecommerce.dto.VehicleSummaryDTO; 
import com.example.ecommerce.model.Wishlist;
import com.example.ecommerce.model.WishlistItem;
import com.example.ecommerce.model.Vehicle;
import com.example.ecommerce.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Map; 

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    // Get current user's wishlist
    @GetMapping
    public ResponseEntity<?> getCurrentUserWishlist() {
        try {
            Wishlist wishlistEntity = wishlistService.getWishlistForCurrentUser();
            WishlistDTO wishlistDTO = convertToDTO(wishlistEntity);
            return ResponseEntity.ok(wishlistDTO);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"User not authenticated\"}");
        } catch (Exception e) {
            System.err.println("Error fetching wishlist: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Internal server error fetching wishlist\"}");
        }
    }

    // Add item to wishlist
    @PostMapping("/items")
    public ResponseEntity<?> addItemToWishlist(@RequestBody AddToWishlistRequest request) {
        try {
            wishlistService.addItemToWishlist(request.getVehicleId());
            // Fetch the updated wishlist to return
            Wishlist updatedWishlist = wishlistService.getWishlistForCurrentUser();
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(updatedWishlist));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"User not authenticated\"}");
        } catch (RuntimeException e) { // Catch vehicle not found, already exists 
            System.err.println("Error adding item to wishlist: " + e.getMessage());
            if (e.getMessage() != null && e.getMessage().contains("already in wishlist")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("{\"error\": \"" + e.getMessage() + "\"}");
            }
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            System.err.println("Unexpected error adding item to wishlist: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Error adding item to wishlist\"}");
        }
    }

    // Remove item from wishlist by WishlistItem ID
    @DeleteMapping("/items/{wishlistItemId}")
    public ResponseEntity<?> removeItemFromWishlist(@PathVariable Long wishlistItemId) {
        try {
            wishlistService.removeItemFromWishlist(wishlistItemId);
            // Fetch and return the updated wishlist
            Wishlist updatedWishlist = wishlistService.getWishlistForCurrentUser();
            return ResponseEntity.ok(convertToDTO(updatedWishlist));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"User not authenticated or cannot modify this item\"}");
        } catch (RuntimeException e) { // Catch item not found
            System.err.println("Error removing item from wishlist: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            System.err.println("Unexpected error removing item from wishlist: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Error removing item from wishlist\"}");
        }
    }

    // --- DTO Conversion Helper Methods ---
    private WishlistDTO convertToDTO(Wishlist wishlist) {
        if (wishlist == null || wishlist.getUser() == null) {
            System.err.println("Warning: Attempted to convert a null wishlist or wishlist with null user.");
            return new WishlistDTO(null, null, List.of());
        }

        List<WishlistItemDTO> itemDTOs = wishlist.getItems().stream()
                .map(this::convertItemToDTO)
                .collect(Collectors.toList());

        return new WishlistDTO(wishlist.getId(), wishlist.getUser().getId(), itemDTOs);
    }

    private WishlistItemDTO convertItemToDTO(WishlistItem item) {
        Vehicle vehicle = item.getVehicle();
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
            System.err.println("Warning: WishlistItem ID " + item.getId() + " references a null Vehicle.");
            
            vehicleDTO = new VehicleSummaryDTO(null, "Unknown", "Unknown", 0, 0.0, null);
        }
        return new WishlistItemDTO(item.getId(), vehicleDTO, item.getAddedAt());
    }

    // Static inner class for request body
    static class AddToWishlistRequest {
        private Long vehicleId;

        public Long getVehicleId() { return vehicleId; }
        public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }
    }
}
