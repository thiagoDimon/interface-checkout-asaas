package com.app.asaas.demo.payment.client;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.app.asaas.demo.payment.dto.CheckoutResponseDTO;
import com.app.asaas.demo.payment.dto.CreateCheckoutDTO;
import com.app.asaas.demo.shared.exception.PaymentGatewayException;

@Component
public class AsaasCheckoutClient {

    private final RestClient restClient;

    public AsaasCheckoutClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public CheckoutResponseDTO createCheckout(CreateCheckoutDTO request) {
        try {
            return restClient.post()
                    .uri("/checkouts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(CheckoutResponseDTO.class);
        } catch (RestClientException exception) {
            throw new PaymentGatewayException("Não foi possível criar o checkout no Asaas", exception);
        }
    }

    public void cancelCheckout(String checkoutId) {
        try {
            restClient.post().uri("/checkouts/{id}/cancel", checkoutId).retrieve().toBodilessEntity();
        } catch (RestClientException exception) {
            throw new PaymentGatewayException("Não foi possível cancelar o checkout no Asaas", exception);
        }
    }

}
