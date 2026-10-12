package com.app.asaas.demo.orders.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.app.asaas.demo.orders.enums.OrderStatusEnum;
import com.app.asaas.demo.payment.enums.BillingTypeEnum;
import com.app.asaas.demo.payment.enums.ChargeTypeEnum;

public record OrderDTO(
        Long id,
        Long productId,
        String productName,
        Long customerId,
        String customerName,
        BigDecimal amount,
        OrderStatusEnum status,
        BillingTypeEnum billingType,
        ChargeTypeEnum chargeType,
        String checkoutUrl,
        Instant createdAt,
        Instant updatedAt) {
}
