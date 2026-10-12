package com.app.asaas.demo.orders.entity;

import java.math.BigDecimal;
import java.time.Instant;

import com.app.asaas.demo.customers.entity.CustomerEntity;
import com.app.asaas.demo.orders.enums.OrderStatusEnum;
import com.app.asaas.demo.payment.enums.BillingTypeEnum;
import com.app.asaas.demo.payment.enums.ChargeTypeEnum;
import com.app.asaas.demo.product.entity.ProductEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class OrdersEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderStatusEnum status = OrderStatusEnum.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "billing_type", nullable = false, updatable = false, length = 20)
    private BillingTypeEnum billingType;

    @Enumerated(EnumType.STRING)
    @Column(name = "charge_type", nullable = false, updatable = false, length = 20)
    private ChargeTypeEnum chargeType;

    @Column(name = "asaas_checkout_id", length = 100)
    private String asaasCheckoutId;

    @Column(name = "checkout_url", length = 500)
    private String checkoutUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    public ProductEntity getProduct() {
        return product;
    }

    public void setProduct(ProductEntity product) {
        this.product = product;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public OrderStatusEnum getStatus() {
        return status;
    }

    public void setStatus(OrderStatusEnum status) {
        this.status = status;
    }

    public BillingTypeEnum getBillingType() {
        return billingType;
    }

    public void setBillingType(BillingTypeEnum billingType) {
        this.billingType = billingType;
    }

    public ChargeTypeEnum getChargeType() {
        return chargeType;
    }

    public void setChargeType(ChargeTypeEnum chargeType) {
        this.chargeType = chargeType;
    }

    public String getAsaasCheckoutId() {
        return asaasCheckoutId;
    }

    public void setAsaasCheckoutId(String asaasCheckoutId) {
        this.asaasCheckoutId = asaasCheckoutId;
    }

    public String getCheckoutUrl() {
        return checkoutUrl;
    }

    public void setCheckoutUrl(String checkoutUrl) {
        this.checkoutUrl = checkoutUrl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

}
