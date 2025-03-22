package com.example.ecommerce.controller;

import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

  @Autowired
  private UserRepository userRepository;

  // Endpoint to register a new user
  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@RequestBody User user) {
    User savedUser = userRepository.save(user);
    return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
  }
  
  // Basic login endpoint with password validation
  @PostMapping("/login")
  public ResponseEntity<?> loginUser(@RequestBody User loginRequest) {
    Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());
    if (userOpt.isPresent()) {
      User user = userOpt.get();
      if (user.getPasswordHash().equals(loginRequest.getPasswordHash())) {
        return ResponseEntity.ok(user);
      } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
      }
    } else {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }
  }
}
