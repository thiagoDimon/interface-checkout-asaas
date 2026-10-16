package com.app.asaas.demo.payment.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.asaas.demo.config.AsaasProperties;
import com.app.asaas.demo.orders.dto.CreateOrderRequestDTO;
import com.app.asaas.demo.orders.dto.OrderDTO;
import com.app.asaas.demo.orders.entity.OrdersEntity;
import com.app.asaas.demo.orders.enums.OrderStatusEnum;
import com.app.asaas.demo.orders.service.OrderService;
import com.app.asaas.demo.payment.dto.AsaasWebhookEventDTO;
import com.app.asaas.demo.payment.dto.CheckoutSessionDTO;
import com.app.asaas.demo.payment.enums.AsaasWebhookEventEnum;
import com.app.asaas.demo.payment.gateway.PaymentGateway;
import com.app.asaas.demo.shared.exception.PaymentGatewayException;

@Service
public class CheckoutService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckoutService.class);

    private final OrderService orderService;
    private final PaymentGateway paymentGateway;
    private final AsaasProperties asaasProperties;

    public CheckoutService(OrderService orderService, PaymentGateway paymentGateway, AsaasProperties asaasProperties) {
        this.orderService = orderService;
        this.paymentGateway = paymentGateway;
        this.asaasProperties = asaasProperties;
    }

    public OrderDTO checkout(CreateOrderRequestDTO request) {
        OrdersEntity order = orderService.createPendingOrder(request);

        try {
            CheckoutSessionDTO checkoutSession = paymentGateway.createCheckout(order);
            return orderService.attachCheckout(order, checkoutSession);
        } catch (PaymentGatewayException exception) {
            orderService.cancelPendingOrder(order);
            throw exception;
        }
    }

    public void cancelOrder(Long orderId) {
        OrdersEntity order = orderService.getPendingOrder(orderId);
        if (order.getAsaasCheckoutId() != null) {
            paymentGateway.cancelCheckout(order.getAsaasCheckoutId());
        }
    }

    public boolean isValidWebhookToken(String token) {
        String expectedToken = asaasProperties.webhookToken();
        if (token == null || expectedToken == null || expectedToken.isBlank()) {
            return false;
        }
        return MessageDigest.isEqual(
                token.getBytes(StandardCharsets.UTF_8),
                expectedToken.getBytes(StandardCharsets.UTF_8));
    }

    @Transactional
    public void processWebhookEvent(AsaasWebhookEventDTO webhook) {
        AsaasWebhookEventEnum webhookEvent = AsaasWebhookEventEnum.fromEvent(webhook.event()).orElse(null);
        String checkoutId = webhook.checkoutId();

        if (webhookEvent == null || checkoutId == null) {
            LOGGER.info("Evento {} ({}) recebido sem alteração de pedido", webhook.event(), webhook.id());
            return;
        }

        OrderStatusEnum newStatus = webhookEvent.getOrderStatus();
        boolean updated = orderService.updateStatusByCheckoutId(checkoutId, newStatus,
                webhookEvent.getAllowedCurrentStatuses());

        if (updated) {
            LOGGER.info("Evento {} ({}): pedido do checkout {} atualizado para {}",
                    webhook.event(), webhook.id(), checkoutId, newStatus);
            return;
        }

        LOGGER.info("Evento {} ({}): nenhum pedido com status compatível para o checkout {}",
                webhook.event(), webhook.id(), checkoutId);
    }

}
