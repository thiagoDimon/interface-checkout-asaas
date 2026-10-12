package com.app.asaas.demo.payment.dto;

import java.math.BigDecimal;
import java.util.List;

import com.app.asaas.demo.payment.enums.BillingTypeEnum;
import com.app.asaas.demo.payment.enums.ChargeTypeEnum;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreateCheckoutDTO(
        List<BillingTypeEnum> billingTypes,
        List<ChargeTypeEnum> chargeTypes,
        Integer minutesToExpire,
        CallbackDTO callback,
        List<ItemDTO> items,
        CustomerDataDTO customerData,
        InstallmentDTO installment) {

    public record CallbackDTO(
            String cancelUrl,
            String expiredUrl,
            String successUrl) {
    }

    public record ItemDTO(
            String name,
            String description,
            String imageBase64,
            Integer quantity,
            BigDecimal value) {
    }

    public record CustomerDataDTO(
            String name,
            String cpfCnpj,
            String email,
            String phone,
            String address,
            String addressNumber,
            String complement,
            String province,
            String postalCode) {
    }

    public record InstallmentDTO(
            Integer maxInstallmentCount) {
    }

}
