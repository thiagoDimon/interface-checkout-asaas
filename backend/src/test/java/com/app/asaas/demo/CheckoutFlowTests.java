package com.app.asaas.demo;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.app.asaas.demo.config.AsaasProperties;
import com.app.asaas.demo.payment.dto.CheckoutSessionDTO;
import com.app.asaas.demo.payment.gateway.PaymentGateway;
import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestConstructor(autowireMode = AutowireMode.ALL)
class CheckoutFlowTests {

    private static final String CHECKOUT_ID = "chk_test_123";
    private static final String CHECKOUT_URL = "https://sandbox.asaas.com/checkoutSession/show/" + CHECKOUT_ID;
    private static final String CREATE_ORDER_BODY = "{\"productId\": 1, \"customerId\": 1, \"billingType\": \"CREDIT_CARD\", \"chargeType\": \"DETACHED\"}";
    private static final String WEBHOOK_BODY = "{\"id\": \"evt_test\", \"event\": \"%s\", \"checkout\": {\"id\": \"%s\"}}";
    private static final String CHARGEBACK_WEBHOOK_BODY = "{\"id\": \"evt_test_chargeback\", \"event\": \"PAYMENT_CHARGEBACK_REQUESTED\", \"payment\": {\"id\": \"pay_test_123\", \"checkoutSession\": \"%s\"}}";

    private final MockMvc mockMvc;
    private final AsaasProperties asaasProperties;

    @MockitoBean
    private PaymentGateway paymentGateway;

    CheckoutFlowTests(MockMvc mockMvc, AsaasProperties asaasProperties) {
        this.mockMvc = mockMvc;
        this.asaasProperties = asaasProperties;
    }

    @BeforeEach
    void mockPaymentGateway() {
        when(paymentGateway.createCheckout(any())).thenReturn(new CheckoutSessionDTO(CHECKOUT_ID, CHECKOUT_URL));
    }

    // Deve marcar um pedido como pago quando o pagamento for aprovado
    @Test
    void shouldMarkOrderAsPaidWhenCheckoutIsPaid() throws Exception {
        assertOrderStatusAfterWebhook("CHECKOUT_PAID", "PAID");
    }

    // Deve marcar um pedido como expirado quando o tempo limite para pagamento for excedido
    @Test
    void shouldMarkOrderAsExpiredWhenCheckoutExpires() throws Exception {
        assertOrderStatusAfterWebhook("CHECKOUT_EXPIRED", "EXPIRED");
    }

    // Deve marcar um pedido como cancelado quando o pagamento for cancelado
    @Test
    void shouldMarkOrderAsCanceledWhenCheckoutIsCanceled() throws Exception {
        assertOrderStatusAfterWebhook("CHECKOUT_CANCELED", "CANCELED");
    }

    // Deve marcar um pedido como cancelado quando é realizado um estorno
    @Test
    void shouldMarkPaidOrderAsCanceledWhenChargebackIsReceived() throws Exception {
        long orderId = createOrder();

        sendWebhook(WEBHOOK_BODY.formatted("CHECKOUT_PAID", CHECKOUT_ID));
        assertOrderStatus(orderId, "PAID");

        sendWebhook(CHARGEBACK_WEBHOOK_BODY.formatted(CHECKOUT_ID));
        assertOrderStatus(orderId, "CANCELED");
    }

    private void assertOrderStatusAfterWebhook(String event, String expectedStatus) throws Exception {
        long orderId = createOrder();
        sendWebhook(WEBHOOK_BODY.formatted(event, CHECKOUT_ID));
        assertOrderStatus(orderId, expectedStatus);
    }

    private void sendWebhook(String body) throws Exception {
        mockMvc.perform(post("/webhooks/asaas")
                .header("asaas-access-token", asaasProperties.webhookToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk());
    }

    private void assertOrderStatus(long orderId, String expectedStatus) throws Exception {
        mockMvc.perform(get("/orders/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(expectedStatus))
                .andExpect(jsonPath("$.amount").value(89.90));
    }

    private long createOrder() throws Exception {
        String response = mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(CREATE_ORDER_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.amount").value(89.90))
                .andExpect(jsonPath("$.checkoutUrl").value(CHECKOUT_URL))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return ((Number) JsonPath.read(response, "$.id")).longValue();
    }

}
