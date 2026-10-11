package com.app.asaas.demo.customers.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.asaas.demo.customers.entity.CustomerEntity;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
    
}
