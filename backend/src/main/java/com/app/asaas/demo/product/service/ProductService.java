package com.app.asaas.demo.product.service;

import com.app.asaas.demo.product.mapper.ProductMapper;
import java.util.List;

import org.springframework.stereotype.Service;

import com.app.asaas.demo.product.dto.ProductDTO;
import com.app.asaas.demo.product.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductMapper productMapper;
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream().map(productMapper::toDto).toList();
    }

}
