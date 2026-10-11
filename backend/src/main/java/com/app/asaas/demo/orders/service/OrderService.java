package com.app.asaas.demo.orders.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.asaas.demo.customers.entity.CustomerEntity;
import com.app.asaas.demo.customers.repository.CustomerRepository;
import com.app.asaas.demo.orders.dto.CreateOrderRequestDTO;
import com.app.asaas.demo.orders.dto.OrderDTO;
import com.app.asaas.demo.orders.entity.OrdersEntity;
import com.app.asaas.demo.orders.mapper.OrderMapper;
import com.app.asaas.demo.orders.repository.OrderRepository;
import com.app.asaas.demo.payment.dto.CheckoutSessionDTO;
import com.app.asaas.demo.product.entity.ProductEntity;
import com.app.asaas.demo.product.repository.ProductRepository;
import com.app.asaas.demo.shared.exception.ResourceNotFoundException;

@Service
public class OrderService {

    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository,
            CustomerRepository customerRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.orderMapper = orderMapper;
    }

    public OrdersEntity createPendingOrder(CreateOrderRequestDTO request) {
        ProductEntity product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Produto", request.productId()));
        CustomerEntity customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", request.customerId()));
        return orderRepository.save(orderMapper.toPendingOrder(product, customer));
    }

    public OrderDTO attachCheckout(OrdersEntity order, CheckoutSessionDTO checkoutSession) {
        order.setAsaasCheckoutId(checkoutSession.checkoutId());
        order.setCheckoutUrl(checkoutSession.checkoutUrl());
        return orderMapper.toDto(orderRepository.saveAndFlush(order));
    }

    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(orderMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", id));
    }

}
