package com.app.asaas.demo.payment.dto;

public record AsaasWebhookEventDTO(
        String id,
        String event,
        CheckoutDTO checkout) {

    public record CheckoutDTO(
            String id,
            String status) {
    }

}
