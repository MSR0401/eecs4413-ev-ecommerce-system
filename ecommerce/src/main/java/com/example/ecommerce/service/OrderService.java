package com.example.ecommerce.service;

import com.example.ecommerce.model.*;
import com.example.ecommerce.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; 
import java.math.BigDecimal; 
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired private OrderRepository orderRepository;
    @Autowired private PaymentRepository paymentRepository;
    @Autowired private CartService cartService;
    @Autowired private VehicleRepository vehicleRepository;

    private static int paymentAttemptCounter = 0;

    @Transactional 
    public Order checkout() {
        Cart cart = cartService.getCartForCurrentUser();
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty. Cannot checkout.");
        }

        // --- 1. Check Stock Availability ---
        for (CartItem cartItem : cart.getItems()) {
            Vehicle vehicle = cartItem.getVehicle();
            if (vehicle == null) {
                throw new RuntimeException("Cart contains an item referencing a non-existent vehicle.");
            }
            if (vehicle.getAvailableStock() < cartItem.getQuantity()) {
                throw new RuntimeException("Insufficient stock for vehicle: " + vehicle.getMake() + " " + vehicle.getModel());
            }
        }

        // --- 2. Create Transient Order and OrderItems ---
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setStatus("pending");

        List<OrderItem> transientOrderItems = new ArrayList<>();
        BigDecimal calculatedTotalPrice = BigDecimal.ZERO; // Initialize total

        for (CartItem cartItem : cart.getItems()) {
            Vehicle vehicle = cartItem.getVehicle();
            OrderItem orderItem = new OrderItem();

            // Set relationship before saving Order
            orderItem.setOrder(order);
            orderItem.setVehicle(vehicle);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPricePerItem(BigDecimal.valueOf(vehicle.getPrice())); 

            // Calculate subtotal explicitly before saving the Order
            BigDecimal itemSubtotal = orderItem.getPricePerItem().multiply(BigDecimal.valueOf(orderItem.getQuantity()));
            orderItem.setSubtotal(itemSubtotal);

            calculatedTotalPrice = calculatedTotalPrice.add(itemSubtotal); // Add to running total
            transientOrderItems.add(orderItem); // Add to list
        }

        // Add the transient items to the transient order's list
        order.setOrderItems(transientOrderItems);

        // --- 3. Set Calculated Total Price on Order ---
        order.setTotalPrice(calculatedTotalPrice); // Set the total *before* the first save

        // --- 4. Save Order (Cascades to OrderItems) ---
        Order savedOrder = orderRepository.save(order);

        // --- 5. Update Vehicle Stock ---
        for (OrderItem savedOrderItem : savedOrder.getOrderItems()) {
            Vehicle vehicle = savedOrderItem.getVehicle();
            if (vehicle != null) {
                vehicle.setAvailableStock(vehicle.getAvailableStock() - savedOrderItem.getQuantity());
                vehicleRepository.save(vehicle);
            } else {
                System.err.println("Warning: OrderItem ID " + savedOrderItem.getId() + " in Order ID " + savedOrder.getId() + " references a null vehicle during stock update.");
            }
        }


        // --- 6. Process Mock Payment ---
        Payment payment = processMockPayment(savedOrder);

        // --- 7. Update Order Status After Successful Payment ---
        savedOrder.setStatus("processing"); 
        Order finalOrder = orderRepository.save(savedOrder);

        // --- 8. Clear the user's cart ---
        cartService.clearCart();

        return finalOrder; // Return the fully processed order
    }

    private Payment processMockPayment(Order order) {
        paymentAttemptCounter++;
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod("MOCK_CHECKOUT");

        if (paymentAttemptCounter % 3 == 0) {
            payment.setStatus("FAILED"); 
            payment.setTransactionId(null);
            paymentRepository.save(payment);
            throw new RuntimeException("Mock Payment Authorization Failed");
        } else {
            payment.setStatus("COMPLETED"); 
            payment.setTransactionId("MOCK_TXN_" + UUID.randomUUID().toString());
            return paymentRepository.save(payment);
        }
    }
}
