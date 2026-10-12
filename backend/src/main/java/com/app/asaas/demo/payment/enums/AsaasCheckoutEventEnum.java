package com.app.asaas.demo.payment.enums;

import java.util.Arrays;
import java.util.Optional;

import com.app.asaas.demo.orders.enums.OrderStatusEnum;

public enum AsaasCheckoutEventEnum {

    CHECKOUT_PAID(OrderStatusEnum.PAID),
    CHECKOUT_EXPIRED(OrderStatusEnum.EXPIRED),
    CHECKOUT_CANCELED(OrderStatusEnum.CANCELED);

    private final OrderStatusEnum orderStatus;

    AsaasCheckoutEventEnum(OrderStatusEnum orderStatus) {
        this.orderStatus = orderStatus;
    }

    public OrderStatusEnum getOrderStatus() {
        return orderStatus;
    }

    public static Optional<OrderStatusEnum> toOrderStatus(String event) {
        return Arrays.stream(values())
                .filter(checkoutEvent -> checkoutEvent.name().equals(event))
                .map(AsaasCheckoutEventEnum::getOrderStatus)
                .findFirst();
    }

}
