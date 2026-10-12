package com.app.asaas.demo.payment.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import com.app.asaas.demo.orders.entity.OrdersEntity;
import com.app.asaas.demo.payment.client.AsaasCheckoutClient;
import com.app.asaas.demo.payment.dto.CheckoutResponseDTO;
import com.app.asaas.demo.payment.dto.CheckoutSessionDTO;
import com.app.asaas.demo.payment.dto.CreateCheckoutDTO;
import com.app.asaas.demo.payment.mapper.CheckoutMapper;
import com.app.asaas.demo.shared.exception.PaymentGatewayException;
import com.app.asaas.demo.shared.util.ImageBase64Util;

@Component
public class AsaasCheckoutGateway implements PaymentGateway {

    @Value("${app.backend-public-url}")
    private String backendPublicUrl;

    private final AsaasCheckoutClient asaasCheckoutClient;
    private final CheckoutMapper checkoutMapper;
    private final ImageBase64Util imageBase64Util;

    public AsaasCheckoutGateway(AsaasCheckoutClient asaasCheckoutClient, CheckoutMapper checkoutMapper,
            ImageBase64Util imageBase64Util) {
        this.asaasCheckoutClient = asaasCheckoutClient;
        this.checkoutMapper = checkoutMapper;
        this.imageBase64Util = imageBase64Util;
    }

    @Override
    public CheckoutSessionDTO createCheckout(OrdersEntity order) {
        String returnUrl = backendPublicUrl + "/checkout/" + order.getId() + "/return";
        CreateCheckoutDTO request = checkoutMapper.toCreateCheckoutDTO(order, loadProductImage(order), returnUrl);
        CheckoutResponseDTO response = asaasCheckoutClient.createCheckout(request);
        if (response == null || response.link() == null) {
            throw new PaymentGatewayException("O Asaas não retornou o link do checkout", null);
        }
        return new CheckoutSessionDTO(response.id(), response.link());
    }

    private String loadProductImage(OrdersEntity order) {
        try {
            return imageBase64Util.downloadAsBase64(order.getProduct().getImage());
        } catch (RestClientException | IllegalArgumentException exception) {
            throw new PaymentGatewayException("Não foi possível carregar a imagem do produto", exception);
        }
    }

}
