package com.example.ecommerce.service;

import com.example.ecommerce.model.*;
import com.example.ecommerce.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class WishlistService {

    @Autowired private WishlistRepository wishlistRepository;
    @Autowired private WishlistItemRepository wishlistItemRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private VehicleRepository vehicleRepository;

    // Get or create wishlist for the current user
    public Wishlist getWishlistForCurrentUser() {
        User currentUser = getCurrentAuthenticatedUser();
        return wishlistRepository.findByUser(currentUser)
                .orElseGet(() -> createWishlistForUser(currentUser));
    }

    private Wishlist createWishlistForUser(User user) {
        Wishlist newWishlist = new Wishlist();
        newWishlist.setUser(user);
        return wishlistRepository.save(newWishlist);
    }

    // Add item to current user's wishlist
    public WishlistItem addItemToWishlist(Long vehicleId) {
        Wishlist wishlist = getWishlistForCurrentUser();
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        // Check if item already exists in wishlist for this user and vehicle
        Optional<WishlistItem> existingItemOpt = wishlistItemRepository
                .findByWishlistIdAndVehicleId(wishlist.getId(), vehicleId);

        if (existingItemOpt.isPresent()) {
            System.out.println("Item already exists in wishlist: Vehicle ID " + vehicleId); // Logging
            return existingItemOpt.get();
        } else {
            // Create and add the new wishlist item
            WishlistItem wishlistItem = new WishlistItem();
            wishlistItem.setWishlist(wishlist);
            wishlistItem.setVehicle(vehicle);

            wishlist.getItems().add(wishlistItem); 

            Wishlist savedWishlist = wishlistRepository.save(wishlist);

            // Fetch the item again to ensure it's managed and has ID
            return savedWishlist.getItems().stream()
                    .filter(item -> item.getVehicle().getId().equals(vehicleId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Wishlist item missing after add operation"));
        }
    }

    // Remove item from current user's wishlist by WishlistItem ID
    public void removeItemFromWishlist(Long wishlistItemId) {
        Wishlist wishlist = getWishlistForCurrentUser();
        WishlistItem itemToRemove = wishlistItemRepository.findById(wishlistItemId)
                .orElseThrow(() -> new RuntimeException("Wishlist item not found with ID: " + wishlistItemId));

        if (!itemToRemove.getWishlist().getId().equals(wishlist.getId())) {
            throw new SecurityException("Cannot remove item from another user's wishlist");
        }

        boolean removed = wishlist.getItems().removeIf(item -> item.getId().equals(wishlistItemId));
        if (!removed) {
            System.err.println("Item with ID " + wishlistItemId + " not found in wishlist collection directly, attempting delete via repository.");
            wishlistItemRepository.delete(itemToRemove); 
            return;
        }

        wishlistRepository.save(wishlist);

    }

    // Remove item from current user's wishlist by Vehicle ID (Convenience method)
    public void removeItemFromWishlistByVehicleId(Long vehicleId) {
        Wishlist wishlist = getWishlistForCurrentUser();
        WishlistItem itemToRemove = wishlistItemRepository.findByWishlistIdAndVehicleId(wishlist.getId(), vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found in wishlist with ID: " + vehicleId));

        boolean removed = wishlist.getItems().removeIf(item -> item.getVehicle().getId().equals(vehicleId));
        if (!removed) {
            System.err.println("Item for Vehicle ID " + vehicleId + " not found in wishlist collection directly, attempting delete via repository.");
            wishlistItemRepository.delete(itemToRemove);
            return;
        }

        wishlistRepository.save(wishlist);
    }


    private User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new SecurityException("User not authenticated");
        }
        String username;
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found in database: " + username));
    }
}
