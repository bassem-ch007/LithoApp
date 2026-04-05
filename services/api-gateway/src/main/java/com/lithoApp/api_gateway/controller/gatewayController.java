package com.lithoApp.api_gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api")
public class gatewayController {
    @GetMapping("/hello")
    public String heloo(){
        return "heloo";
    }
}
