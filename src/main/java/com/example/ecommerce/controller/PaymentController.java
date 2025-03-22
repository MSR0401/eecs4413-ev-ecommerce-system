package com.example.ecommerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private static int paymentCounter = 0;

    @PostMapping
    public ResponseEntity<PaymentResponse> processPayment() {
        paymentCounter++;
        if (paymentCounter % 3 == 0) {
            // Deny every third payment request
            return ResponseEntity.status(402)
                    .body(new PaymentResponse("Credit Card Authorization Failed", null));
        } else {
            // Approve payment
            String transactionId = "txn_" + System.currentTimeMillis();
            return ResponseEntity.ok(new PaymentResponse("Order Successfully Completed", transactionId));
        }
    }

    // Response DTO for payment processing
    public static class PaymentResponse {
        private String message;
        private String transactionId;

        public PaymentResponse() {}

        public PaymentResponse(String message, String transactionId) {
            this.message = message;
            this.transactionId = transactionId;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }
    }
}
