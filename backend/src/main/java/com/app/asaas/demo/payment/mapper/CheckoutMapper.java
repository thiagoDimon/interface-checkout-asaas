package com.app.asaas.demo.payment.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.app.asaas.demo.customers.entity.CustomerEntity;
import com.app.asaas.demo.orders.entity.OrdersEntity;
import com.app.asaas.demo.payment.dto.CreateCheckoutDTO;
import com.app.asaas.demo.payment.dto.CreateCheckoutDTO.CallbackDTO;
import com.app.asaas.demo.payment.dto.CreateCheckoutDTO.CustomerDataDTO;
import com.app.asaas.demo.payment.dto.CreateCheckoutDTO.InstallmentDTO;
import com.app.asaas.demo.payment.dto.CreateCheckoutDTO.ItemDTO;
import com.app.asaas.demo.payment.enums.ChargeTypeEnum;
import com.app.asaas.demo.product.entity.ProductEntity;

@Component
public class CheckoutMapper {

    private static final int MINUTES_TO_EXPIRE = 60;
    private static final int MAX_INSTALLMENT_COUNT = 12;
    private static final int ITEM_QUANTITY = 1;

    public CreateCheckoutDTO toCreateCheckoutDTO(OrdersEntity order, String imageBase64, String returnUrl) {
        return new CreateCheckoutDTO(
                List.of(order.getBillingType()),
                toChargeTypes(order.getChargeType()),
                MINUTES_TO_EXPIRE,
                new CallbackDTO(returnUrl, returnUrl, returnUrl),
                List.of(toItem(order, imageBase64)),
                toCustomerData(order.getCustomer()),
                toInstallment(order.getChargeType()));
    }

    // O Asaas exige DETACHED junto com INSTALLMENT na cobrança parcelada.
    private List<ChargeTypeEnum> toChargeTypes(ChargeTypeEnum chargeType) {
        return chargeType == ChargeTypeEnum.INSTALLMENT
                ? List.of(ChargeTypeEnum.DETACHED, ChargeTypeEnum.INSTALLMENT)
                : List.of(ChargeTypeEnum.DETACHED);
    }

    private ItemDTO toItem(OrdersEntity order, String imageBase64) {
        ProductEntity product = order.getProduct();
        return new ItemDTO(
                product.getName(),
                product.getDescription(),
                imageBase64,
                ITEM_QUANTITY,
                order.getAmount());
    }

    private CustomerDataDTO toCustomerData(CustomerEntity customer) {
        return new CustomerDataDTO(
                customer.getName(),
                customer.getCpf(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getAddress(),
                customer.getAddressNumber(),
                customer.getComplement(),
                customer.getProvince(),
                customer.getPostalCode());
    }

    private InstallmentDTO toInstallment(ChargeTypeEnum chargeType) {
        return chargeType == ChargeTypeEnum.INSTALLMENT ? new InstallmentDTO(MAX_INSTALLMENT_COUNT) : null;
    }

}
