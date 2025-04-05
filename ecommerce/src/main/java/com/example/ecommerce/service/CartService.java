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
public class CartService {

    @Autowired private CartRepository cartRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private VehicleRepository vehicleRepository;

    // Get or create cart for the current user
    public Cart getCartForCurrentUser() {
        User currentUser = getCurrentAuthenticatedUser();
        return cartRepository.findByUser(currentUser)
                .orElseGet(() -> createCartForUser(currentUser));
    }

    private Cart createCartForUser(User user) {
        Cart newCart = new Cart();
        newCart.setUser(user);
        return cartRepository.save(newCart);
    }

    // Add item to current user's cart
    public CartItem addItemToCart(Long vehicleId, int quantity) {
        Cart cart = getCartForCurrentUser();
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        if (vehicle.getAvailableStock() < quantity) {
            throw new RuntimeException("Not enough stock available");
        }

        // Check if item already exists in cart
        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getVehicle().getId().equals(vehicleId))
                .findFirst();

        CartItem cartItem;
        if (existingItemOpt.isPresent()) {
            cartItem = existingItemOpt.get();
            // Check total quantity against stock
            if (vehicle.getAvailableStock() < cartItem.getQuantity() + quantity) {
                throw new RuntimeException("Not enough stock available for the requested total quantity");
            }
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setVehicle(vehicle);
            cartItem.setQuantity(quantity);
            cart.addItem(cartItem); 
        }

        cartRepository.save(cart);
        return cart.getItems().stream()
                .filter(item -> item.getVehicle().getId().equals(vehicleId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cart item missing after add operation")); // Should not happen
    }


    // Remove item from current user's cart
    public void removeItemFromCart(Long cartItemId) {
        Cart cart = getCartForCurrentUser();
        CartItem itemToRemove = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        // Security check: ensure the item belongs to the current user's cart
        if (!itemToRemove.getCart().getId().equals(cart.getId())) {
            throw new SecurityException("Cannot remove item from another user's cart");
        }

        cart.removeItem(itemToRemove);
        cartRepository.save(cart); 

    
    }

    // Get the currently authenticated user
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
                .orElseThrow(() -> new RuntimeException("Authenticated user not found in database"));
    }

    // Clear the cart for the current user
    public void clearCart() {
        Cart cart = getCartForCurrentUser();
        cartItemRepository.deleteAll(cart.getItems());
        cart.getItems().clear(); // Clear the collection in the Cart entity
        cartRepository.save(cart); // Save the cart with the empty item list
    }
}
