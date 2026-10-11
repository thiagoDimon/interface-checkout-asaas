package com.app.asaas.demo.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.asaas.demo.orders.dto.CreateOrderRequestDTO;
import com.app.asaas.demo.orders.dto.OrderDTO;
import com.app.asaas.demo.orders.entity.OrdersEntity;
import com.app.asaas.demo.orders.service.OrderService;
import com.app.asaas.demo.payment.dto.CheckoutSessionDTO;
import com.app.asaas.demo.payment.gateway.PaymentGateway;

@Service
public class CheckoutService {

    private final OrderService orderService;
    private final PaymentGateway paymentGateway;

    public CheckoutService(OrderService orderService, PaymentGateway paymentGateway) {
        this.orderService = orderService;
        this.paymentGateway = paymentGateway;
    }

    @Transactional
    public OrderDTO checkout(CreateOrderRequestDTO request) {
        OrdersEntity order = orderService.createPendingOrder(request);
        CheckoutSessionDTO checkoutSession = paymentGateway.createCheckout(order);
        return orderService.attachCheckout(order, checkoutSession);
    }

}
