package com.app.asaas.demo.orders.dto;

import com.app.asaas.demo.payment.enums.BillingTypeEnum;
import com.app.asaas.demo.payment.enums.ChargeTypeEnum;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateOrderRequestDTO(
        @NotNull(message = "O produto é obrigatório")
        @Positive(message = "O produto informado é inválido")
        Long productId,

        @NotNull(message = "O cliente é obrigatório")
        @Positive(message = "O cliente informado é inválido")
        Long customerId,

        @NotNull(message = "A forma de pagamento é obrigatória")
        BillingTypeEnum billingType,

        @NotNull(message = "O tipo de cobrança é obrigatório")
        ChargeTypeEnum chargeType) {

    @AssertTrue(message = "Pagamento via Pix só pode ser à vista")
    public boolean isPaymentCombinationValid() {
        return billingType != BillingTypeEnum.PIX || chargeType != ChargeTypeEnum.INSTALLMENT;
    }

}
