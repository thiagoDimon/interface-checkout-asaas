package com.app.asaas.demo.customers.mapper;

import org.springframework.stereotype.Component;

import com.app.asaas.demo.customers.dto.CustomerDTO;
import com.app.asaas.demo.customers.entity.CustomerEntity;

@Component
public class CustomerMapper {

    public CustomerDTO toDto(CustomerEntity customer) {
        return new CustomerDTO(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getCpf());
    }

}
