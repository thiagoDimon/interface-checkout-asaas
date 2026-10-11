package com.app.asaas.demo.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.asaas.demo.orders.entity.OrdersEntity;

public interface OrderRepository extends JpaRepository<OrdersEntity, Long> {

}
