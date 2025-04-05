package com.example.ecommerce.repository;

import com.example.ecommerce.model.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {
    Optional<WishlistItem> findByWishlistIdAndVehicleId(Long wishlistId, Long vehicleId);
    void deleteByWishlistIdAndVehicleId(Long wishlistId, Long vehicleId);
}
