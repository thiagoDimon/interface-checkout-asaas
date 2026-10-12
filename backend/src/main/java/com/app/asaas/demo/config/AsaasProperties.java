package com.app.asaas.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "asaas")
public record AsaasProperties(
        String baseUrl,
        String apiKey,
        String webhookToken) {
}
