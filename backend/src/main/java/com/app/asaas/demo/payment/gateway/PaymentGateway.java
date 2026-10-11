package com.app.asaas.demo.payment.gateway;

import com.app.asaas.demo.orders.entity.OrdersEntity;
import com.app.asaas.demo.payment.dto.CheckoutSessionDTO;

public interface PaymentGateway {

    CheckoutSessionDTO createCheckout(OrdersEntity order);

}
