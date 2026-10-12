<template>
  <section id="orders-view" class="orders-view">
    <header id="orders-view-header" class="orders-view__header">
      <h2 id="orders-view-title" class="orders-view__title">Meus pedidos</h2>
      <BaseButton id="orders-view-back-button" variant="secondary" @click="goToCatalog">
        Voltar para a loja
      </BaseButton>
    </header>

    <BaseAlert
      v-if="customerError"
      id="orders-customer-error"
      variant="error"
      title="Cliente indisponível"
    >
      {{ customerError }}
    </BaseAlert>

    <div v-else-if="loading" id="orders-loading" class="orders-view__loading">
      <BaseLoader id="orders-loader" size="large" label="Carregando pedidos..." />
    </div>

    <BaseAlert
      v-else-if="error"
      id="orders-error"
      variant="error"
      title="Não foi possível carregar os pedidos"
    >
      {{ error }}
    </BaseAlert>

    <BaseAlert v-else-if="orders.length === 0" id="orders-empty" variant="info">
      Você ainda não fez nenhum pedido.
    </BaseAlert>

    <OrderList v-else id="orders-list" :orders="orders" @select="goToOrder" />
  </section>
</template>

<script>
import { mapActions, mapState } from 'pinia'
import BaseAlert from '@/components/base/BaseAlert.vue'
import BaseButton from '@/components/base/BaseButton.vue'
import BaseLoader from '@/components/base/BaseLoader.vue'
import OrderList from '@/components/order/OrderList.vue'
import { useCustomerStore } from '@/stores/customerStore'
import { useOrderStore } from '@/stores/orderStore'

export default {
  name: 'OrdersView',
  components: {
    BaseAlert,
    BaseButton,
    BaseLoader,
    OrderList,
  },
  computed: {
    ...mapState(useOrderStore, ['orders', 'loading', 'error']),
    ...mapState(useCustomerStore, {
      customer: 'customer',
      customerError: 'error',
    }),
  },
  watch: {
    customer: {
      immediate: true,
      handler(customer) {
        if (customer) {
          this.fetchOrdersByCustomerId(customer.id)
        }
      },
    },
  },
  methods: {
    ...mapActions(useOrderStore, ['fetchOrdersByCustomerId']),

    goToOrder(order) {
      this.$router.push({ name: 'order-status', params: { id: order.id } })
    },

    goToCatalog() {
      this.$router.push({ name: 'catalog' })
    },
  },
}
</script>

<style scoped>
.orders-view {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
  max-width: 56rem;
  margin: 0 auto;
}

.orders-view__header {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-md);
}

.orders-view__title {
  margin: 0;
  font-size: 1.75rem;
  font-weight: 700;
}

.orders-view__loading {
  display: flex;
  justify-content: center;
  padding: var(--spacing-xl) 0;
  color: var(--color-primary);
}
</style>
