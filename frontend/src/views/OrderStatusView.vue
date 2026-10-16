<template>
  <section id="order-status-view" class="order-status-view">
    <header id="order-status-header" class="order-status-view__header">
      <button
        id="order-status-back-button"
        type="button"
        class="order-status-view__back"
        aria-label="Voltar para a loja"
        @click="goToCatalog"
      >
        <svg
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
          stroke-linecap="round"
          stroke-linejoin="round"
          aria-hidden="true"
        >
          <path d="M19 12H5M12 19l-7-7 7-7" />
        </svg>
      </button>
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

      <BaseAlert v-if="isAwaitingCancellation" id="order-status-cancel-message" variant="info">
        Aguarde enquanto cancelamos o seu pedido...
      </BaseAlert>
      <BaseAlert v-else id="order-status-message" :variant="statusConfig.alertVariant">
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
          <dt>Pagamento</dt>
          <dd id="order-status-payment">{{ paymentLabel }}</dd>
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

      <template v-if="isPending && !isAwaitingCancellation" #footer>
        <div class="order-status-view__footer">
          <p
            v-if="canGoToCheckout"
            id="order-status-checkout-hint"
            class="order-status-view__checkout-hint"
          >
            Já pagou? Aguarde a confirmação. O botão abre o mesmo checkout do pedido e não gera uma
            nova cobrança.
          </p>

          <div class="order-status-view__actions">
            <BaseButton
              v-if="isPending"
              id="order-status-cancel-button"
              variant="secondary"
              @click="openCancelModal"
            >
              Cancelar pedido
            </BaseButton>
            <BaseButton
              v-if="canGoToCheckout"
              id="order-status-checkout-button"
              variant="cta"
              :loading="isOpeningCheckout"
              @click="goToCheckout"
            >
              Ir para o pagamento
            </BaseButton>
          </div>
        </div>
      </template>
    </BaseCard>

    <BaseConfirmModal
      id="order-cancel-modal"
      :open="isCancelModalOpen"
      title="Cancelar pedido"
      confirm-label="Cancelar pedido"
      confirm-variant="danger"
      :loading="cancelling"
      :error-message="cancelError ?? ''"
      @close="closeCancelModal"
      @confirm="confirmCancel"
    >
      Tem certeza que deseja cancelar o pedido #{{ id }}? O link de pagamento será invalidado no
      Asaas e o pedido não poderá mais ser pago.
    </BaseConfirmModal>
  </section>
</template>

<script>
import { mapActions, mapState } from 'pinia'
import BaseAlert from '@/components/base/BaseAlert.vue'
import BaseButton from '@/components/base/BaseButton.vue'
import BaseCard from '@/components/base/BaseCard.vue'
import BaseConfirmModal from '@/components/base/BaseConfirmModal.vue'
import BaseLoader from '@/components/base/BaseLoader.vue'
import OrderStatusBadge from '@/components/order/OrderStatusBadge.vue'
import { ORDER_STATUS } from '@/constants/orderStatus'
import OrderStatusEnum from '@/enums/orderStatus'
import { useOrderStore } from '@/stores/orderStore'
import { formatCurrency, formatDateTime, formatPaymentMethod } from '@/utils/formatters'

const POLLING_INTERVAL_MS = 5000

export default {
  name: 'OrderStatusView',
  components: {
    BaseAlert,
    BaseButton,
    BaseCard,
    BaseConfirmModal,
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
      isOpeningCheckout: false,
      isCancelModalOpen: false,
      isCancelRequested: false,
    }
  },
  computed: {
    ...mapState(useOrderStore, ['currentOrder', 'loading', 'error', 'cancelling', 'cancelError']),
    order() {
      return this.currentOrder?.id === Number(this.id) ? this.currentOrder : null
    },
    statusConfig() {
      return ORDER_STATUS[this.order.status]
    },
    paymentLabel() {
      return formatPaymentMethod(this.order.billingType, this.order.chargeType)
    },
    canGoToCheckout() {
      return this.isPending && Boolean(this.order.checkoutUrl)
    },
    isPending() {
      return this.order?.status === OrderStatusEnum.PENDING
    },
    isAwaitingCancellation() {
      return this.isCancelRequested && this.isPending
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
    ...mapActions(useOrderStore, ['fetchOrderById', 'cancelOrder', 'clearError']),
    formatCurrency,
    formatDateTime,

    async loadOrder() {
      await this.fetchOrderById(this.id)

      if (this.isActive && this.isPending) {
        this.pollingTimer = setTimeout(this.loadOrder, POLLING_INTERVAL_MS)
      }
    },

    async goToCheckout() {
      this.isOpeningCheckout = true

      await this.fetchOrderById(this.id)

      if (!this.canGoToCheckout) {
        this.isOpeningCheckout = false
        return
      }

      window.location.assign(this.order.checkoutUrl)
    },

    openCancelModal() {
      this.clearError()
      this.isCancelModalOpen = true
    },

    closeCancelModal() {
      this.isCancelModalOpen = false
    },

    async confirmCancel() {
      const cancelled = await this.cancelOrder(this.id)
      if (cancelled) {
        this.isCancelRequested = true
        this.isCancelModalOpen = false
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

.order-status-view__header {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.order-status-view__back {
  display: inline-flex;
  flex-shrink: 0;
  align-items: center;
  justify-content: center;
  width: 2.25rem;
  height: 2.25rem;
  padding: 0;
  border: none;
  border-radius: var(--radius-full);
  background-color: transparent;
  color: var(--color-text-muted);
  cursor: pointer;
  transition:
    background-color var(--transition-fast),
    color var(--transition-fast);
}

.order-status-view__back:hover {
  background-color: var(--color-neutral-soft);
  color: var(--color-text);
}

.order-status-view__back:focus-visible {
  outline: 2px solid var(--color-primary);
  outline-offset: 2px;
}

.order-status-view__back svg {
  width: 1.5rem;
  height: 1.5rem;
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

.order-status-view__footer {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.order-status-view__checkout-hint {
  margin: 0;
  color: var(--color-text-muted);
  font-size: 0.875rem;
}

.order-status-view__actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: var(--spacing-sm);
}

.order-status-view__amount {
  font-size: 1.25rem;
  font-weight: 700;
  font-variant-numeric: tabular-nums;
}
</style>
