package com.app.asaas.demo.orders.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.app.asaas.demo.orders.enums.OrderStatusEnum;

public record OrderDTO(
        Long id,
        Long productId,
        String productName,
        Long customerId,
        String customerName,
        BigDecimal amount,
        OrderStatusEnum status,
        String checkoutUrl,
        Instant createdAt,
        Instant updatedAt) {
}
