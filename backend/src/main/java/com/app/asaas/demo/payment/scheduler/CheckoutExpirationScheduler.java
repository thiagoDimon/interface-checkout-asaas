package com.app.asaas.demo.payment.scheduler;

import java.time.Duration;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.app.asaas.demo.orders.service.OrderService;
import com.app.asaas.demo.payment.mapper.CheckoutMapper;

@Component
public class CheckoutExpirationScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckoutExpirationScheduler.class);

    private static final Duration EXPIRATION_MARGIN = Duration.ofMinutes(10);

    private final OrderService orderService;

    public CheckoutExpirationScheduler(OrderService orderService) {
        this.orderService = orderService;
    }

    @Scheduled(fixedDelayString = "PT5M")
    public void expireStalePendingOrders() {
        Instant limit = Instant.now()
                .minus(Duration.ofMinutes(CheckoutMapper.MINUTES_TO_EXPIRE))
                .minus(EXPIRATION_MARGIN);

        int expiredOrders = orderService.expirePendingOrdersCreatedBefore(limit);
        if (expiredOrders > 0) {
            LOGGER.info("{} pedido(s) pendente(s) expirado(s) sem webhook do Asaas", expiredOrders);
        }
    }

}
