<template>
  <BaseModal :id="id" :open="open" :title="title" size="large" @close="handleClose">
    <div v-if="product" class="product-details">
      <div v-if="product.image" class="product-details__media">
        <img
          :id="`${id}-image`"
          class="product-details__image"
          :src="product.image"
          :alt="product.name"
        />
      </div>

      <div class="product-details__info">
        <h3 class="product-details__section-title">Descrição</h3>
        <p :id="`${id}-description`" class="product-details__description">
          {{ description }}
        </p>
      </div>
    </div>

    <template #footer>
      <div class="product-details__footer">
        <BaseButton
          :id="`${id}-buy-button`"
          class="product-details__buy-button"
          variant="cta"
          :disabled="disabled"
          @click="handleBuy"
        >
          Comprar
        </BaseButton>
      </div>
    </template>
  </BaseModal>
</template>

<script>
import BaseButton from '@/components/base/BaseButton.vue'
import BaseModal from '@/components/base/BaseModal.vue'

const EMPTY_DESCRIPTION_MESSAGE = 'Este produto não possui descrição.'

export default {
  name: 'ProductDetailsModal',
  components: {
    BaseButton,
    BaseModal,
  },
  props: {
    id: {
      type: String,
      required: true,
    },
    open: {
      type: Boolean,
      default: false,
    },
    product: {
      type: Object,
      default: null,
    },
    disabled: {
      type: Boolean,
      default: false,
    },
  },
  emits: ['close', 'buy'],
  computed: {
    title() {
      return this.product?.name ?? ''
    },
    description() {
      return this.product?.description || EMPTY_DESCRIPTION_MESSAGE
    },
  },
  methods: {
    handleClose() {
      this.$emit('close')
    },
    handleBuy() {
      this.$emit('buy', this.product)
    },
  },
}
</script>

<style scoped>
.product-details {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(0, 1fr);
  gap: var(--spacing-xl);
  align-items: start;
}

.product-details__media {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-lg);
  background-color: var(--color-surface-muted);
  border-radius: var(--radius-md);
}

.product-details__image {
  width: 100%;
  max-height: 26rem;
  object-fit: contain;
}

.product-details__section-title {
  margin: 0 0 var(--spacing-sm);
  color: var(--color-text-muted);
  font-size: 0.75rem;
  font-weight: 600;
  letter-spacing: 0.04em;
  text-transform: uppercase;
}

.product-details__description {
  margin: 0;
  font-size: 1rem;
  line-height: 1.6;
}

.product-details__footer {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.product-details__buy-button {
  align-self: flex-end;
  min-width: 12rem;
}

@media (max-width: 40rem) {
  .product-details {
    grid-template-columns: 1fr;
    gap: var(--spacing-lg);
  }

  .product-details__image {
    max-height: 16rem;
  }

  .product-details__buy-button {
    align-self: stretch;
  }
}
</style>
