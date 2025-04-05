package com.example.ecommerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {
  @RequestMapping("/")
  public String home() {
    // Forward the request to index.html in the static folder
    return "forward:/index.html";
  }
}
