package com.example.ecommerce.controller;

import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @GetMapping("/me")
  public ResponseEntity<?> getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
      // No one is logged in or user is anonymous
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("authenticated", false));
    }

    String userEmail;
    Object principal = authentication.getPrincipal();

    if (principal instanceof UserDetails) {
      userEmail = ((UserDetails) principal).getUsername();
    } else {
      userEmail = principal.toString();
    }


    // Return basic info indicating logged-in status and email
    return ResponseEntity.ok(Map.of(
            "authenticated", true,
            "email", userEmail
    ));
  }


  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@RequestBody User user) {
    if (userRepository.findByEmail(user.getEmail()).isPresent()) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
    }
    user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
    user.setCreatedAt(LocalDateTime.now());
    if (user.getRole() == null || user.getRole().isEmpty()){
      user.setRole("customer");
    }
    User savedUser = userRepository.save(user);
    savedUser.setPasswordHash(null);
    return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
  }

  @PostMapping("/login")
  public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
    Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());
    if (userOpt.isPresent()) {
      User user = userOpt.get();
      if (passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
        user.setPasswordHash(null);
        return ResponseEntity.ok(user);
      } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
      }
    } else {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }
  }

  public static class LoginRequest {
    private String email;
    private String password;
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
  }
}
