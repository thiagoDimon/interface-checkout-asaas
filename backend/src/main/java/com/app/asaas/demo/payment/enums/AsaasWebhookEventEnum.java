package com.app.asaas.demo.payment.enums;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import com.app.asaas.demo.orders.enums.OrderStatusEnum;

public enum AsaasWebhookEventEnum {

    CHECKOUT_PAID(OrderStatusEnum.PAID, Set.of(OrderStatusEnum.PENDING, OrderStatusEnum.EXPIRED)),
    CHECKOUT_EXPIRED(OrderStatusEnum.EXPIRED, Set.of(OrderStatusEnum.PENDING)),
    CHECKOUT_CANCELED(OrderStatusEnum.CANCELED, Set.of(OrderStatusEnum.PENDING)),
    PAYMENT_CHARGEBACK_REQUESTED(OrderStatusEnum.CANCELED, Set.of(OrderStatusEnum.PAID)),
    PAYMENT_REFUNDED(OrderStatusEnum.CANCELED, Set.of(OrderStatusEnum.PAID));

    private final OrderStatusEnum orderStatus;
    private final Set<OrderStatusEnum> allowedCurrentStatuses;

    AsaasWebhookEventEnum(OrderStatusEnum orderStatus, Set<OrderStatusEnum> allowedCurrentStatuses) {
        this.orderStatus = orderStatus;
        this.allowedCurrentStatuses = allowedCurrentStatuses;
    }

    public OrderStatusEnum getOrderStatus() {
        return orderStatus;
    }

    public Set<OrderStatusEnum> getAllowedCurrentStatuses() {
        return allowedCurrentStatuses;
    }

    public static Optional<AsaasWebhookEventEnum> fromEvent(String event) {
        return Arrays.stream(values())
                .filter(webhookEvent -> webhookEvent.name().equals(event))
                .findFirst();
    }

}
