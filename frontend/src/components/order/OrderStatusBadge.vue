<template>
  <BaseBadge :id="id" :variant="statusConfig.badgeVariant">
    {{ statusConfig.label }}
  </BaseBadge>
</template>

<script>
import BaseBadge from '@/components/base/BaseBadge.vue'
import { ORDER_STATUS } from '@/constants/orderStatus'

export default {
  name: 'OrderStatusBadge',
  components: {
    BaseBadge,
  },
  props: {
    id: {
      type: String,
      required: true,
    },
    status: {
      type: String,
      required: true,
      validator: (value) => Object.keys(ORDER_STATUS).includes(value),
    },
  },
  computed: {
    statusConfig() {
      return ORDER_STATUS[this.status]
    },
  },
}
</script>
