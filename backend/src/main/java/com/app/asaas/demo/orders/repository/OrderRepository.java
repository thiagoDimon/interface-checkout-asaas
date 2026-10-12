package com.app.asaas.demo.orders.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.asaas.demo.orders.entity.OrdersEntity;
import com.app.asaas.demo.orders.enums.OrderStatusEnum;

public interface OrderRepository extends JpaRepository<OrdersEntity, Long> {

    Optional<OrdersEntity> findByAsaasCheckoutId(String asaasCheckoutId);

    List<OrdersEntity> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    List<OrdersEntity> findByStatusAndCreatedAtBefore(OrderStatusEnum status, Instant createdAt);

    long countByCustomerIdAndCreatedAtAfter(Long customerId, Instant createdAt);

}
