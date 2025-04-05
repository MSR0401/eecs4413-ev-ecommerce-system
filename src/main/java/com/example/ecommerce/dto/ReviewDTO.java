package com.example.ecommerce.dto;

import java.time.LocalDateTime;

public class ReviewDTO {

    private Long id;
    private Long vehicleId; // ID of the vehicle being reviewed
    private Long userId;    // ID of the user who wrote the review (can be null)
    private String userName;
    private Integer rating;
    private String comment;
    private String reviewerName; 
    private LocalDateTime createdAt;


    // Default constructor (needed by some frameworks)
    public ReviewDTO() {
    }

    public ReviewDTO(Long id, Long vehicleId, Long userId, String userName, Integer rating, String comment, String reviewerName, LocalDateTime createdAt) {
        this.id = id;
        this.vehicleId = vehicleId;
        this.userId = userId;
        this.userName = userName;
        this.rating = rating;
        this.comment = comment;
        this.reviewerName = reviewerName;
        this.createdAt = createdAt;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getReviewerName() {
        if (this.reviewerName != null && !this.reviewerName.trim().isEmpty()) {
            return this.reviewerName;
        } else if (this.userName != null && !this.userName.trim().isEmpty()) {
            return this.userName; 
        } else {
            return "Anonymous";
        }
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
