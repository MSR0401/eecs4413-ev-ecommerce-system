package com.example.ecommerce.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    @GetMapping("/response")
    public ChatbotResponse getResponse(@RequestParam String query) {
        String responseMessage = "You asked: " + query + ". How can I assist further?";
        return new ChatbotResponse(responseMessage);
    }

    // Response DTO for chatbot
    public static class ChatbotResponse {
        private String response;

        public ChatbotResponse() {}

        public ChatbotResponse(String response) {
            this.response = response;
        }

        public String getResponse() {
            return response;
        }

        public void setResponse(String response) {
            this.response = response;
        }
    }
}