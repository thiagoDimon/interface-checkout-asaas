<template>
  <BaseCard :id="id" hoverable>
    <template v-if="product.image" #media>
      <img
        :id="`${id}-image`"
        class="product-card__image"
        :src="product.image"
        :alt="product.name"
      />
    </template>

    <h3 :id="`${id}-name`" class="product-card__name">{{ product.name }}</h3>
    <p v-if="product.description" :id="`${id}-description`" class="product-card__description">
      {{ product.description }}
    </p>
    <p :id="`${id}-price`" class="product-card__price">{{ formattedPrice }}</p>

    <template #footer>
      <div class="product-card__actions">
        <BaseButton :id="`${id}-view-button`" variant="secondary" block @click="handleView">
          Visualizar
        </BaseButton>
        <BaseButton
          :id="`${id}-buy-button`"
          variant="cta"
          block
          :loading="loading"
          :disabled="disabled"
          @click="handleBuy"
        >
          Comprar
        </BaseButton>
      </div>
    </template>
  </BaseCard>
</template>

<script>
import BaseButton from '@/components/base/BaseButton.vue'
import BaseCard from '@/components/base/BaseCard.vue'
import { formatCurrency } from '@/utils/formatCurrency'

export default {
  name: 'ProductCard',
  components: {
    BaseButton,
    BaseCard,
  },
  props: {
    id: {
      type: String,
      required: true,
    },
    product: {
      type: Object,
      required: true,
    },
    loading: {
      type: Boolean,
      default: false,
    },
    disabled: {
      type: Boolean,
      default: false,
    },
  },
  emits: ['buy', 'view'],
  computed: {
    formattedPrice() {
      return formatCurrency(this.product.price)
    },
  },
  methods: {
    handleBuy() {
      this.$emit('buy', this.product)
    },
    handleView() {
      this.$emit('view', this.product)
    },
  },
}
</script>

<style scoped>
.product-card__image {
  width: 100%;
  height: 12rem;
  object-fit: contain;
}

.product-card__name {
  margin: 0;
  font-size: 1rem;
  font-weight: 600;
  line-height: 1.4;
}

.product-card__description {
  display: -webkit-box;
  margin: var(--spacing-xs) 0 0;
  overflow: hidden;
  color: var(--color-text-muted);
  font-size: 0.875rem;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-card__price {
  margin: var(--spacing-sm) 0 0;
  font-size: 1.5rem;
  font-weight: 700;
  font-variant-numeric: tabular-nums;
}

.product-card__actions {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(7rem, 1fr));
  gap: var(--spacing-sm);
}
</style>
