package com.example.ecommerce.config;

import com.example.ecommerce.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.User;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> {
            com.example.ecommerce.model.User appUser = userRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

            // Ensure role is not null before converting to uppercase
            String role = appUser.getRole() != null ? appUser.getRole().toUpperCase() : "CUSTOMER"; // Default role if null

            return User.builder()
                    .username(appUser.getEmail())
                    .password(appUser.getPasswordHash())
                    .roles(role)
                    .build();
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/login.html",
                                "/register.html",
                                "/vehicles.html",
                                "/vehicle-detail.html",
                                "/loanCalculator.html",
                                "/cart.html",
                                "/checkout.html",
                                "/wishlist.html", // <-- Add wishlist.html
                                "/*.css",
                                "/*.js",
                                "/*.png",
                                "/*.jpg",
                                "/*.ico"
                        ).permitAll()

                        // Allow public access to specific API endpoints (registration, public data)
                        .requestMatchers(HttpMethod.POST, "/api/users/register").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users/me").permitAll() // Allow checking login status
                        .requestMatchers(HttpMethod.GET, "/api/loans/calculate").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/vehicles", "/api/vehicles/").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/vehicles/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/vehicles/{vehicleId}/reviews").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/vehicles/{vehicleId}/reviews").permitAll() // Allow anonymous reviews

                        // --- SECURE CART, ORDER & WISHLIST ENDPOINTS ---
                        .requestMatchers("/api/cart/**").authenticated()
                        .requestMatchers("/api/orders/checkout").authenticated()
                        .requestMatchers("/api/wishlist/**").authenticated() // <-- Add wishlist security

                        // Admin reports
                        .requestMatchers("/api/reports/**").hasRole("ADMIN")

                        // Default: Require authentication for any other request
                        .anyRequest().authenticated()
                )
                // Configure form login
                .formLogin(form -> form
                        .loginPage("/login.html")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/vehicles.html", true)
                        .failureUrl("/login.html?error=true")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .permitAll()
                )
                // Configure logout
                .logout(logout -> logout
                        .logoutUrl("/api/users/logout")
                        .logoutSuccessUrl("/login.html?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }
}