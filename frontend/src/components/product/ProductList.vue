<template>
  <ul :id="id" class="product-list">
    <li
      v-for="product in products"
      :id="`${id}-item-${product.id}`"
      :key="product.id"
      class="product-list__item"
    >
      <ProductCard
        :id="`product-card-${product.id}`"
        :product="product"
        :loading="buyingProductId === product.id"
        :disabled="buyDisabled"
        @buy="handleBuy"
        @view="handleView"
      />
    </li>
  </ul>
</template>

<script>
import ProductCard from './ProductCard.vue'

export default {
  name: 'ProductList',
  components: {
    ProductCard,
  },
  props: {
    id: {
      type: String,
      required: true,
    },
    products: {
      type: Array,
      required: true,
    },
    buyingProductId: {
      type: Number,
      default: null,
    },
    buyDisabled: {
      type: Boolean,
      default: false,
    },
  },
  emits: ['buy', 'view'],
  methods: {
    handleBuy(product) {
      this.$emit('buy', product)
    },
    handleView(product) {
      this.$emit('view', product)
    },
  },
}
</script>

<style scoped>
.product-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(15rem, 1fr));
  gap: var(--spacing-lg);
  margin: 0;
  padding: 0;
  list-style: none;
}

.product-list__item {
  display: flex;
}

.product-list__item > * {
  flex: 1;
}
</style>
