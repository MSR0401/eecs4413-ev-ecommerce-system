package com.example.ecommerce.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    @GetMapping("/calculate")
    public LoanResponse calculateLoan(
            @RequestParam double price,
            @RequestParam double downPayment,
            @RequestParam double interestRate,
            @RequestParam int duration) {

        double principal = price - downPayment;
        double monthlyRate = interestRate / 100.0 / 12;
        double monthlyPayment;

        if (monthlyRate == 0) {
            monthlyPayment = principal / duration;
        } else {
            monthlyPayment = principal * monthlyRate * Math.pow(1 + monthlyRate, duration) /
                    (Math.pow(1 + monthlyRate, duration) - 1);
        }

        return new LoanResponse(String.format("%.2f", monthlyPayment));
    }

    // Response DTO for loan calculation
    public static class LoanResponse {
        private String monthlyPayment;

        public LoanResponse() {}

        public LoanResponse(String monthlyPayment) {
            this.monthlyPayment = monthlyPayment;
        }

        public String getMonthlyPayment() {
            return monthlyPayment;
        }

        public void setMonthlyPayment(String monthlyPayment) {
            this.monthlyPayment = monthlyPayment;
        }
    }
}