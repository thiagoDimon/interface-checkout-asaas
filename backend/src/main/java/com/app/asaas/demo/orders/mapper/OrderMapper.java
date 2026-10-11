package com.app.asaas.demo.orders.mapper;

import org.springframework.stereotype.Component;

import com.app.asaas.demo.customers.entity.CustomerEntity;
import com.app.asaas.demo.orders.dto.OrderDTO;
import com.app.asaas.demo.orders.entity.OrdersEntity;
import com.app.asaas.demo.orders.enums.OrderStatusEnum;
import com.app.asaas.demo.product.entity.ProductEntity;

@Component
public class OrderMapper {

    public OrderDTO toDto(OrdersEntity order) {
        return new OrderDTO(
                order.getId(),
                order.getProduct().getId(),
                order.getProduct().getName(),
                order.getCustomer().getId(),
                order.getCustomer().getName(),
                order.getAmount(),
                order.getStatus(),
                order.getCheckoutUrl(),
                order.getCreatedAt(),
                order.getUpdatedAt());
    }

    public OrdersEntity toPendingOrder(ProductEntity product, CustomerEntity customer) {
        OrdersEntity order = new OrdersEntity();
        order.setProduct(product);
        order.setCustomer(customer);
        order.setAmount(product.getPrice());
        order.setStatus(OrderStatusEnum.PENDING);
        return order;
    }

}
