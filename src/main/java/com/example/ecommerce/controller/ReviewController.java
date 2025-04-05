// Example: src/main/java/com/example/ecommerce/controller/ReviewController.java
package com.example.ecommerce.controller;

import com.example.ecommerce.dto.ReviewDTO; // Import the DTO
import com.example.ecommerce.model.Review;
import com.example.ecommerce.model.User;
import com.example.ecommerce.model.Vehicle;
import com.example.ecommerce.repository.ReviewRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime; // Make sure LocalDateTime is imported
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // Import Collectors

@RestController
@RequestMapping("/api")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    // POST a new review for a vehicle
    @PostMapping("/vehicles/{vehicleId}/reviews")
    public ResponseEntity<?> addReview(@PathVariable Long vehicleId, @RequestBody Review reviewRequest) { // Using Review entity for request body for now

        Optional<Vehicle> vehicleOpt = vehicleRepository.findById(vehicleId);
        if (!vehicleOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vehicle not found");
        }

        reviewRequest.setVehicle(vehicleOpt.get()); // Associate the vehicle

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = null;

        // Determine if user is logged in
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            Object principal = authentication.getPrincipal();
            String username;
            if (principal instanceof UserDetails) {
                username = ((UserDetails) principal).getUsername();
            } else {
                username = principal.toString();
            }
            currentUser = userRepository.findByEmail(username).orElse(null);
        }

        // Assign user or handle anonymous review name
        if (currentUser != null) {
            reviewRequest.setUser(currentUser); // Link logged-in user
            reviewRequest.setReviewerName(null); // Clear anonymous name if logged in
        } else {
            reviewRequest.setUser(null); // Ensure user is null for anonymous
            // Reviewer name should be set in reviewRequest from the Request Body
            if (reviewRequest.getReviewerName() == null || reviewRequest.getReviewerName().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Reviewer name is required for anonymous reviews.");
            }
        }

        // Basic validation for rating
        if (reviewRequest.getRating() == null || reviewRequest.getRating() < 1 || reviewRequest.getRating() > 5) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Rating must be between 1 and 5.");
        }

        // *** REMOVED the problematic line: reviewRequest.onCreate(); ***
        // The @PrePersist annotation in Review.java handles setting createdAt automatically before saving.
        // If @PrePersist wasn't there, you would set it explicitly *here*:
        // reviewRequest.setCreatedAt(LocalDateTime.now());

        Review savedReview = reviewRepository.save(reviewRequest);

        // Convert the saved entity to DTO for the response
        ReviewDTO savedReviewDTO = convertToDTO(savedReview);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedReviewDTO); // Return DTO
    }

    // GET all reviews for a specific vehicle - Returns List<ReviewDTO>
    @GetMapping("/vehicles/{vehicleId}/reviews")
    public ResponseEntity<List<ReviewDTO>> getReviewsForVehicle(@PathVariable Long vehicleId) {
        if (!vehicleRepository.existsById(vehicleId)) { // Check if vehicle exists
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Vehicle not found
        }

        List<Review> reviews = reviewRepository.findByVehicleIdOrderByCreatedAtDesc(vehicleId); // Fetch Review entities

        // Convert List<Review> to List<ReviewDTO>
        List<ReviewDTO> reviewDTOs = reviews.stream()
                .map(this::convertToDTO) // Use a helper method for conversion
                .collect(Collectors.toList());

        return ResponseEntity.ok(reviewDTOs); // Return the list of DTOs
    }

    // --- HELPER METHOD TO CONVERT Review entity to ReviewDTO ---
    private ReviewDTO convertToDTO(Review review) {
        Long userId = null;
        String userName = null;
        if (review.getUser() != null) { // Check if the user object exists
            userId = review.getUser().getId(); // Get user ID
            userName = review.getUser().getName(); // Get user name
        }

        Long vehicleId = null;
        if (review.getVehicle() != null) { // Check if the vehicle object exists
            vehicleId = review.getVehicle().getId(); // Get vehicle ID
        }


        return new ReviewDTO(
                review.getId(),
                vehicleId,
                userId,
                userName,
                review.getRating(),
                review.getComment(),
                review.getReviewerName(), // Anonymous reviewer name
                review.getCreatedAt()     // createdAt should be set by @PrePersist
        );
    }
}