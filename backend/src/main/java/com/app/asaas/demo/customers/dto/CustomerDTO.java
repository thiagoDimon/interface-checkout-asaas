package com.app.asaas.demo.customers.dto;

public record CustomerDTO(
        Long id,
        String name,
        String email,
        String cpf) {
}
