package com.app.asaas.demo.payment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.asaas.demo.payment.dto.AsaasWebhookEventDTO;
import com.app.asaas.demo.payment.service.CheckoutService;

@RestController
@RequestMapping("/webhooks/asaas")
public class AsaasWebhookController {

    private final CheckoutService checkoutService;

    public AsaasWebhookController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @PostMapping
    public ResponseEntity<Void> receiveEvent(@RequestHeader(name = "asaas-access-token", required = false) String token,
            @RequestBody AsaasWebhookEventDTO event) {
        if (!checkoutService.isValidWebhookToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        checkoutService.processWebhookEvent(event);
        return ResponseEntity.ok().build();
    }

}
