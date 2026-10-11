package com.app.asaas.demo.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.asaas.demo.product.entity.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

}
