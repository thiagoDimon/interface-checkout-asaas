package com.app.asaas.demo.product.mapper;

import org.springframework.stereotype.Component;

import com.app.asaas.demo.product.dto.ProductDTO;
import com.app.asaas.demo.product.entity.ProductEntity;

@Component
public class ProductMapper {

    public ProductDTO toDto(ProductEntity product) {
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getImage());
    }

}
