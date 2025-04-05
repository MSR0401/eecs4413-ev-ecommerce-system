package com.example.ecommerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatbotController {

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatMessage message) {
        String userMessage = message.getContent().toLowerCase();

        String reply;
        if (userMessage.contains("return")) {
            reply = "You can return items by going to your account > Orders > Return Item.";
        } else if (userMessage.contains("shipping")) {
            reply = "Shipping typically takes 3 to 5 business days.";
        } else if (userMessage.contains("refund")) {
            reply = "Refunds are processed within 5 to 7 business days.";
        } else {
            reply = "Sorry, I didn’t quite catch that. Could you try rephrasing?";
        }

        return ResponseEntity.ok(new ChatResponse(reply));
    }

    // Message request model
    static class ChatMessage {
        private String content;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    // Message response model
    static class ChatResponse {
        private String reply;

        public ChatResponse(String reply) {
            this.reply = reply;
        }

        public String getReply() {
            return reply;
        }
    }
}
