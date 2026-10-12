package com.app.asaas.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class AsaasCheckoutConfig {

    private final AsaasProperties asaasProperties;

    public AsaasCheckoutConfig(AsaasProperties asaasProperties) {
        this.asaasProperties = asaasProperties;
    }

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl(asaasProperties.baseUrl())
                .defaultHeader("access_token", asaasProperties.apiKey())
                .build();
    }

}
