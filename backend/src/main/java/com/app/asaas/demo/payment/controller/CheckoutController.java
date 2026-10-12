package com.app.asaas.demo.payment.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/checkout")
public class CheckoutController {

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @GetMapping("/{orderId}/return")
    public ResponseEntity<Void> returnFromCheckout(@PathVariable Long orderId) {
        URI orderPage = URI.create(frontendUrl + "/orders/" + orderId);
        return ResponseEntity.status(HttpStatus.FOUND).location(orderPage).build();
    }

}
