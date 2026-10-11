package com.app.asaas.demo.customers.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.app.asaas.demo.customers.dto.CustomerDTO;
import com.app.asaas.demo.customers.mapper.CustomerMapper;
import com.app.asaas.demo.customers.repository.CustomerRepository;

@Service
public class CustomerService {

    private final CustomerMapper customerMapper;
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    public Optional<CustomerDTO> getCustomerById(Long id) {
        return customerRepository.findById(id).map(customerMapper::toDto);
    }

}
