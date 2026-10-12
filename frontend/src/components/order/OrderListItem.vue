<template>
  <BaseCard :id="id" tag="div">
    <div class="order-list-item">
      <div class="order-list-item__main">
        <span :id="`${id}-number`" class="order-list-item__number">Pedido #{{ order.id }}</span>
        <h3 :id="`${id}-product-name`" class="order-list-item__product-name">
          {{ order.productName }}
        </h3>
        <span :id="`${id}-created-at`" class="order-list-item__meta">
          {{ formatDateTime(order.createdAt) }} · {{ paymentLabel }}
        </span>
      </div>

      <div class="order-list-item__side">
        <OrderStatusBadge :id="`${id}-status`" :status="order.status" />
        <strong :id="`${id}-amount`" class="order-list-item__amount">
          {{ formatCurrency(order.amount) }}
        </strong>
        <BaseButton :id="`${id}-details-button`" variant="secondary" @click="handleSelect">
          Ver detalhes
        </BaseButton>
      </div>
    </div>
  </BaseCard>
</template>

<script>
import BaseButton from '@/components/base/BaseButton.vue'
import BaseCard from '@/components/base/BaseCard.vue'
import OrderStatusBadge from '@/components/order/OrderStatusBadge.vue'
import { formatCurrency, formatDateTime, formatPaymentMethod } from '@/utils/formatters'

export default {
  name: 'OrderListItem',
  components: {
    BaseButton,
    BaseCard,
    OrderStatusBadge,
  },
  props: {
    id: {
      type: String,
      required: true,
    },
    order: {
      type: Object,
      required: true,
    },
  },
  emits: ['select'],
  computed: {
    paymentLabel() {
      return formatPaymentMethod(this.order.billingType, this.order.chargeType)
    },
  },
  methods: {
    formatCurrency,
    formatDateTime,
    handleSelect() {
      this.$emit('select', this.order)
    },
  },
}
</script>

<style scoped>
.order-list-item {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-md);
}

.order-list-item__main {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
  min-width: 0;
}

.order-list-item__number {
  color: var(--color-text-muted);
  font-size: 0.75rem;
  font-weight: 600;
  letter-spacing: 0.04em;
  text-transform: uppercase;
}

.order-list-item__product-name {
  margin: 0;
  font-size: 1rem;
  font-weight: 600;
}

.order-list-item__meta {
  color: var(--color-text-muted);
  font-size: 0.875rem;
}

.order-list-item__side {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: var(--spacing-md);
}

.order-list-item__amount {
  font-size: 1.125rem;
  font-variant-numeric: tabular-nums;
}
</style>
