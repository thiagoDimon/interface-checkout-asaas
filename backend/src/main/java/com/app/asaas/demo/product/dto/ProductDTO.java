package com.app.asaas.demo.product.dto;

import java.math.BigDecimal;

public record ProductDTO(
        Long id,
        String name,
        String description,
        BigDecimal price,
        String image) {
}
