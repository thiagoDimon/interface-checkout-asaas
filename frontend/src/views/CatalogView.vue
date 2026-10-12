<template>
  <section id="catalog-view" class="catalog-view">
    <header id="catalog-view-header" class="catalog-view__header">
      <h2 id="catalog-view-title" class="catalog-view__title">Produtos</h2>
      <p id="catalog-view-subtitle" class="catalog-view__subtitle">
        Escolha um produto e finalize o pagamento com segurança no checkout do Asaas.
      </p>
    </header>

    <div class="catalog-view__alerts">
      <BaseAlert
        v-if="customerError"
        id="catalog-customer-error"
        variant="error"
        title="Cliente indisponível"
      >
        {{ customerError }}
      </BaseAlert>

      <BaseAlert
        v-if="checkoutError"
        id="catalog-checkout-error"
        variant="error"
        title="Não foi possível iniciar o pagamento"
      >
        {{ checkoutError }}
      </BaseAlert>
    </div>

    <div v-if="productsLoading" id="catalog-loading" class="catalog-view__loading">
      <BaseLoader id="catalog-loader" size="large" label="Carregando produtos..." />
    </div>

    <BaseAlert
      v-else-if="productsError"
      id="catalog-products-error"
      variant="error"
      title="Erro ao carregar os produtos"
    >
      <p id="catalog-products-error-message" class="catalog-view__error-message">
        {{ productsError }}
      </p>
      <BaseButton id="catalog-retry-button" variant="secondary" @click="fetchProducts">
        Tentar novamente
      </BaseButton>
    </BaseAlert>

    <BaseAlert v-else-if="!hasProducts" id="catalog-empty" variant="info">
      Nenhum produto disponível no momento.
    </BaseAlert>

    <ProductList
      v-else
      id="catalog-product-list"
      :products="products"
      :buying-product-id="buyingProductId"
      :buy-disabled="isBuyDisabled"
      @buy="openPaymentOptions"
      @view="openProductDetails"
    />

    <ProductDetailsModal
      id="product-details-modal"
      :open="isProductDetailsOpen"
      :product="selectedProduct"
      :disabled="isBuyDisabled"
      @close="closeProductDetails"
      @buy="openPaymentOptions"
    />

    <PaymentOptionsModal
      id="payment-options-modal"
      :open="isPaymentOptionsOpen"
      :product="paymentProduct"
      :loading="isPaymentProductBuying"
      :disabled="isBuyDisabled"
      :error-message="checkoutError ?? ''"
      @close="closePaymentOptions"
      @confirm="handleBuy"
    />
  </section>
</template>

<script>
import { mapActions, mapState } from 'pinia'
import BaseAlert from '@/components/base/BaseAlert.vue'
import BaseButton from '@/components/base/BaseButton.vue'
import BaseLoader from '@/components/base/BaseLoader.vue'
import PaymentOptionsModal from '@/components/checkout/PaymentOptionsModal.vue'
import ProductDetailsModal from '@/components/product/ProductDetailsModal.vue'
import ProductList from '@/components/product/ProductList.vue'
import { useCustomerStore } from '@/stores/customerStore'
import { useOrderStore } from '@/stores/orderStore'
import { useProductStore } from '@/stores/productStore'

const MISSING_CHECKOUT_URL_MESSAGE = 'O link de pagamento não foi gerado. Tente novamente.'

export default {
  name: 'CatalogView',
  components: {
    BaseAlert,
    BaseButton,
    BaseLoader,
    PaymentOptionsModal,
    ProductDetailsModal,
    ProductList,
  },
  data() {
    return {
      redirectingProductId: null,
      redirectError: null,
      selectedProduct: null,
      isProductDetailsOpen: false,
      paymentProduct: null,
      isPaymentOptionsOpen: false,
    }
  },
  computed: {
    ...mapState(useProductStore, {
      products: 'products',
      hasProducts: 'hasProducts',
      productsLoading: 'loading',
      productsError: 'error',
    }),
    ...mapState(useCustomerStore, {
      customer: 'customer',
      hasCustomer: 'hasCustomer',
      customerError: 'error',
    }),
    ...mapState(useOrderStore, {
      creatingProductId: 'creatingProductId',
      isCreatingOrder: 'isCreatingOrder',
      orderError: 'error',
    }),
    buyingProductId() {
      return this.creatingProductId ?? this.redirectingProductId
    },
    isBuyDisabled() {
      return !this.hasCustomer || this.buyingProductId !== null
    },
    isPaymentProductBuying() {
      return this.paymentProduct !== null && this.buyingProductId === this.paymentProduct.id
    },
    checkoutError() {
      return this.orderError ?? this.redirectError
    },
  },
  async created() {
    await this.fetchProducts()
  },
  methods: {
    ...mapActions(useProductStore, ['fetchProducts']),
    ...mapActions(useOrderStore, ['createOrder']),
    ...mapActions(useOrderStore, { clearOrderError: 'clearError' }),

    openProductDetails(product) {
      this.redirectError = null
      this.clearOrderError()
      this.selectedProduct = product
      this.isProductDetailsOpen = true
    },

    closeProductDetails() {
      this.isProductDetailsOpen = false
    },

    openPaymentOptions(product) {
      this.redirectError = null
      this.clearOrderError()
      this.isProductDetailsOpen = false
      this.paymentProduct = product
      this.isPaymentOptionsOpen = true
    },

    closePaymentOptions() {
      this.isPaymentOptionsOpen = false
    },

    async handleBuy({ billingType, chargeType }) {
      const product = this.paymentProduct
      this.redirectError = null

      const order = await this.createOrder({
        productId: product.id,
        customerId: this.customer.id,
        billingType,
        chargeType,
      })

      if (!order) {
        return
      }

      if (!order.checkoutUrl) {
        this.redirectError = MISSING_CHECKOUT_URL_MESSAGE
        return
      }

      this.redirectingProductId = product.id
      window.location.assign(order.checkoutUrl)
    },
  },
}
</script>

<style scoped>
.catalog-view {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.catalog-view__title {
  margin: 0;
  font-size: 1.75rem;
  font-weight: 700;
}

.catalog-view__subtitle {
  margin: var(--spacing-xs) 0 0;
  color: var(--color-text-muted);
}

.catalog-view__alerts {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.catalog-view__alerts:empty {
  display: none;
}

.catalog-view__loading {
  display: flex;
  justify-content: center;
  padding: var(--spacing-xl) 0;
  color: var(--color-primary);
}

.catalog-view__error-message {
  margin: 0 0 var(--spacing-sm);
}
</style>
