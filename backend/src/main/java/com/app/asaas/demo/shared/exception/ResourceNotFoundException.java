package com.app.asaas.demo.shared.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, Long id) {
        super(resourceName + " com id " + id + " não encontrado(a)");
    }

}
