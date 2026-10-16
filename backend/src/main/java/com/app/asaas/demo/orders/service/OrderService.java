package com.app.asaas.demo.orders.service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.asaas.demo.customers.entity.CustomerEntity;
import com.app.asaas.demo.customers.repository.CustomerRepository;
import com.app.asaas.demo.orders.dto.CreateOrderRequestDTO;
import com.app.asaas.demo.orders.dto.OrderDTO;
import com.app.asaas.demo.orders.entity.OrdersEntity;
import com.app.asaas.demo.orders.enums.OrderStatusEnum;
import com.app.asaas.demo.orders.mapper.OrderMapper;
import com.app.asaas.demo.orders.repository.OrderRepository;
import com.app.asaas.demo.payment.dto.CheckoutSessionDTO;
import com.app.asaas.demo.product.entity.ProductEntity;
import com.app.asaas.demo.product.repository.ProductRepository;
import com.app.asaas.demo.shared.exception.BusinessRuleException;
import com.app.asaas.demo.shared.exception.ResourceNotFoundException;
import com.app.asaas.demo.shared.exception.TooManyRequestsException;

@Service
public class OrderService {

    private static final int MAX_ORDERS_PER_WINDOW = 3;
    private static final Duration ORDER_RATE_LIMIT_WINDOW = Duration.ofMinutes(1);

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

    @Transactional
    public OrdersEntity createPendingOrder(CreateOrderRequestDTO request) {
        ProductEntity product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Produto", request.productId()));
        CustomerEntity customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", request.customerId()));
        validateOrderRateLimit(customer.getId());
        return orderRepository.save(orderMapper.toPendingOrder(product, customer, request));
    }

    @Transactional
    public OrderDTO attachCheckout(OrdersEntity order, CheckoutSessionDTO checkoutSession) {
        order.setAsaasCheckoutId(checkoutSession.checkoutId());
        order.setCheckoutUrl(checkoutSession.checkoutUrl());
        return orderMapper.toDto(orderRepository.saveAndFlush(order));
    }

    @Transactional(readOnly = true)
    public OrdersEntity getPendingOrder(Long id) {
        OrdersEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", id));
        if (order.getStatus() != OrderStatusEnum.PENDING) {
            throw new BusinessRuleException("Somente pedidos pendentes podem ser cancelados");
        }
        return order;
    }

    @Transactional
    public OrderDTO cancelPendingOrder(OrdersEntity order) {
        OrdersEntity savedOrder = orderRepository.findById(order.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", order.getId()));
        if (savedOrder.getStatus() == OrderStatusEnum.PENDING) {
            savedOrder.setStatus(OrderStatusEnum.CANCELED);
        }
        return orderMapper.toDto(savedOrder);
    }

    @Transactional
    public boolean updateStatusByCheckoutId(String checkoutId, OrderStatusEnum status,
            Set<OrderStatusEnum> allowedCurrentStatuses) {
        return orderRepository.findByAsaasCheckoutId(checkoutId)
                .filter(order -> allowedCurrentStatuses.contains(order.getStatus()))
                .map(order -> {
                    order.setStatus(status);
                    return true;
                })
                .orElse(false);
    }

    @Transactional
    public int expirePendingOrdersCreatedBefore(Instant limit) {
        List<OrdersEntity> staleOrders = orderRepository.findByStatusAndCreatedAtBefore(OrderStatusEnum.PENDING, limit);
        staleOrders.forEach(order -> order.setStatus(OrderStatusEnum.EXPIRED));
        return staleOrders.size();
    }

    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(orderMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", id));
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByCustomerId(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Cliente", customerId);
        }

        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)
                .stream()
                .map(orderMapper::toDto)
                .toList();
    }

    private void validateOrderRateLimit(Long customerId) {
        Instant windowStart = Instant.now().minus(ORDER_RATE_LIMIT_WINDOW);
        if (orderRepository.countByCustomerIdAndCreatedAtAfter(customerId, windowStart) >= MAX_ORDERS_PER_WINDOW) {
            throw new TooManyRequestsException("Muitas tentativas de compra. Tente novamente em um minuto.");
        }
    }

}
