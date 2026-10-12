<template>
  <section id="order-status-view" class="order-status-view">
    <header id="order-status-header" class="order-status-view__header">
      <h2 id="order-status-title" class="order-status-view__title">Pedido #{{ id }}</h2>
    </header>

    <div v-if="loading && !order" id="order-status-loading" class="order-status-view__loading">
      <BaseLoader id="order-status-loader" size="large" label="Carregando pedido..." />
    </div>

    <BaseAlert
      v-else-if="error && !order"
      id="order-status-error"
      variant="error"
      title="Não foi possível carregar o pedido"
    >
      {{ error }}
    </BaseAlert>

    <BaseCard v-if="order" id="order-status-card" tag="div">
      <div class="order-status-view__summary">
        <h3 id="order-status-product-name" class="order-status-view__product-name">
          {{ order.productName }}
        </h3>
        <OrderStatusBadge id="order-status-badge" :status="order.status" />
      </div>

      <BaseAlert id="order-status-message" :variant="statusConfig.alertVariant">
        {{ statusConfig.message }}
      </BaseAlert>

      <dl id="order-status-details" class="order-status-view__details">
        <div class="order-status-view__detail">
          <dt>Valor</dt>
          <dd id="order-status-amount" class="order-status-view__amount">
            {{ formatCurrency(order.amount) }}
          </dd>
        </div>
        <div class="order-status-view__detail">
          <dt>Cliente</dt>
          <dd id="order-status-customer-name">{{ order.customerName }}</dd>
        </div>
        <div class="order-status-view__detail">
          <dt>Criado em</dt>
          <dd id="order-status-created-at">{{ formatDateTime(order.createdAt) }}</dd>
        </div>
        <div class="order-status-view__detail">
          <dt>Atualizado em</dt>
          <dd id="order-status-updated-at">{{ formatDateTime(order.updatedAt) }}</dd>
        </div>
      </dl>

      <template #footer>
        <BaseButton id="order-status-back-button" variant="secondary" @click="goToCatalog">
          Voltar para a loja
        </BaseButton>
      </template>
    </BaseCard>
  </section>
</template>

<script>
import { mapActions, mapState } from 'pinia'
import BaseAlert from '@/components/base/BaseAlert.vue'
import BaseButton from '@/components/base/BaseButton.vue'
import BaseCard from '@/components/base/BaseCard.vue'
import BaseLoader from '@/components/base/BaseLoader.vue'
import OrderStatusBadge from '@/components/order/OrderStatusBadge.vue'
import { ORDER_STATUS } from '@/constants/orderStatus'
import { useOrderStore } from '@/stores/orderStore'
import { formatCurrency } from '@/utils/formatCurrency'
import { formatDateTime } from '@/utils/formatDateTime'

const POLLING_INTERVAL_MS = 5000

export default {
  name: 'OrderStatusView',
  components: {
    BaseAlert,
    BaseButton,
    BaseCard,
    BaseLoader,
    OrderStatusBadge,
  },
  props: {
    id: {
      type: String,
      required: true,
    },
  },
  data() {
    return {
      pollingTimer: null,
      isActive: true,
    }
  },
  computed: {
    ...mapState(useOrderStore, ['currentOrder', 'loading', 'error']),
    order() {
      return this.currentOrder?.id === Number(this.id) ? this.currentOrder : null
    },
    statusConfig() {
      return ORDER_STATUS[this.order.status]
    },
    isPending() {
      return this.order?.status === 'PENDING'
    },
  },
  async created() {
    await this.loadOrder()
  },
  beforeUnmount() {
    this.isActive = false
    clearTimeout(this.pollingTimer)
  },
  methods: {
    ...mapActions(useOrderStore, ['fetchOrderById']),
    formatCurrency,
    formatDateTime,

    async loadOrder() {
      await this.fetchOrderById(this.id)

      if (this.isActive && this.isPending) {
        this.pollingTimer = setTimeout(this.loadOrder, POLLING_INTERVAL_MS)
      }
    },

    goToCatalog() {
      this.$router.push({ name: 'catalog' })
    },
  },
}
</script>

<style scoped>
.order-status-view {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
  max-width: 40rem;
  margin: 0 auto;
}

.order-status-view__title {
  margin: 0;
  font-size: 1.75rem;
  font-weight: 700;
}

.order-status-view__loading {
  display: flex;
  justify-content: center;
  padding: var(--spacing-xl) 0;
  color: var(--color-primary);
}

.order-status-view__summary {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-md);
}

.order-status-view__product-name {
  margin: 0;
  font-size: 1.125rem;
  font-weight: 600;
}

.order-status-view__details {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(10rem, 1fr));
  gap: var(--spacing-md);
  margin: var(--spacing-lg) 0 0;
}

.order-status-view__detail dt {
  color: var(--color-text-muted);
  font-size: 0.75rem;
  font-weight: 600;
  letter-spacing: 0.04em;
  text-transform: uppercase;
}

.order-status-view__detail dd {
  margin: var(--spacing-xs) 0 0;
}

.order-status-view__amount {
  font-size: 1.25rem;
  font-weight: 700;
  font-variant-numeric: tabular-nums;
}
</style>
