package com.app.asaas.demo.orders.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateOrderRequestDTO(
        @NotNull(message = "O produto é obrigatório")
        @Positive(message = "O produto informado é inválido")
        Long productId,

        @NotNull(message = "O cliente é obrigatório")
        @Positive(message = "O cliente informado é inválido")
        Long customerId) {
}
