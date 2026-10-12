package com.app.asaas.demo.orders.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.asaas.demo.orders.entity.OrdersEntity;

public interface OrderRepository extends JpaRepository<OrdersEntity, Long> {

    Optional<OrdersEntity> findByAsaasCheckoutId(String asaasCheckoutId);

    List<OrdersEntity> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

}
