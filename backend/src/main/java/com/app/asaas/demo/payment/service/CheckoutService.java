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
import com.app.asaas.demo.payment.enums.AsaasCheckoutEventEnum;
import com.app.asaas.demo.payment.gateway.PaymentGateway;

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

    @Transactional
    public OrderDTO checkout(CreateOrderRequestDTO request) {
        OrdersEntity order = orderService.createPendingOrder(request);
        CheckoutSessionDTO checkoutSession = paymentGateway.createCheckout(order);
        return orderService.attachCheckout(order, checkoutSession);
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
        OrderStatusEnum newStatus = AsaasCheckoutEventEnum.toOrderStatus(webhook.event()).orElse(null);
        if (newStatus == null || webhook.checkout() == null) {
            LOGGER.info("Evento {} ({}) recebido sem alteração de pedido", webhook.event(), webhook.id());
            return;
        }

        String message = "";
        String checkoutId = webhook.checkout().id();

        boolean updated = orderService.updateStatusByCheckoutId(checkoutId, newStatus);
        if (updated) {
            message = "Evento {} ({}): pedido do checkout {} atualizado para {}";
            LOGGER.info(message, webhook.event(), webhook.id(), checkoutId, newStatus);
            return;
        }

        message = "Evento {} ({}): nenhum pedido pendente para o checkout {}";
        LOGGER.info(message, webhook.event(), webhook.id(), checkoutId);
    }

}
