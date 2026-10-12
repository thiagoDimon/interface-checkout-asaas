<template>
  <ul :id="id" class="order-list">
    <li v-for="order in orders" :id="`${id}-item-${order.id}`" :key="order.id">
      <OrderListItem :id="`order-list-item-${order.id}`" :order="order" @select="handleSelect" />
    </li>
  </ul>
</template>

<script>
import OrderListItem from './OrderListItem.vue'

export default {
  name: 'OrderList',
  components: {
    OrderListItem,
  },
  props: {
    id: {
      type: String,
      required: true,
    },
    orders: {
      type: Array,
      required: true,
    },
  },
  emits: ['select'],
  methods: {
    handleSelect(order) {
      this.$emit('select', order)
    },
  },
}
</script>

<style scoped>
.order-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
  margin: 0;
  padding: 0;
  list-style: none;
}
</style>
