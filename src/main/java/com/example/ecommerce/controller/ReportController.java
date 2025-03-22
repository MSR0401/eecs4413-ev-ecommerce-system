package com.example.ecommerce.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @GetMapping("/sales")
    public ReportResponse getSalesReport() {
        return new ReportResponse("Sales Report Data: Total sales, vehicle details, etc.");
    }

    @GetMapping("/inventory")
    public ReportResponse getInventoryReport() {
        return new ReportResponse("Inventory Report Data: List of vehicles, stock counts, etc.");
    }

    // Response DTO for reports
    public static class ReportResponse {
        private String report;

        public ReportResponse() {}

        public ReportResponse(String report) {
            this.report = report;
        }

        public String getReport() {
            return report;
        }

        public void setReport(String report) {
            this.report = report;
        }
    }
}
