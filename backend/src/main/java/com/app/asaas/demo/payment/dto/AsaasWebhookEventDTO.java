package com.app.asaas.demo.payment.dto;

public record AsaasWebhookEventDTO(
        String id,
        String event,
        CheckoutDTO checkout,
        PaymentDTO payment) {

    public record CheckoutDTO(
            String id,
            String status) {
    }

    public record PaymentDTO(
            String id,
            String checkoutSession) {
    }

    public String checkoutId() {
        if (checkout != null) {
            return checkout.id();
        }
        return payment != null ? payment.checkoutSession() : null;
    }

}
