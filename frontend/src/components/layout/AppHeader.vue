<template>
  <header :id="id" class="app-header">
    <div class="app-header__container">
      <RouterLink :id="`${id}-home-link`" to="/" class="app-header__brand">
        <h1 :id="`${id}-title`" class="app-header__title">Asaas Demo Checkout Store</h1>
      </RouterLink>

      <BaseLoader v-if="customerLoading" :id="`${id}-customer-loader`" size="small" />
      <CustomerSummary v-else-if="customer" :id="`${id}-customer-summary`" :customer="customer" />
    </div>
  </header>
</template>

<script>
import { mapState } from 'pinia'

import BaseLoader from '@/components/base/BaseLoader.vue'
import CustomerSummary from '@/components/customer/CustomerSummary.vue'
import { useCustomerStore } from '@/stores/customerStore'

export default {
  name: 'AppHeader',
  components: {
    BaseLoader,
    CustomerSummary,
  },
  props: {
    id: {
      type: String,
      required: true,
    },
  },
  computed: {
    ...mapState(useCustomerStore, {
      customer: 'customer',
      customerLoading: 'loading',
    }),
  },
}
</script>

<style scoped>
.app-header {
  background-color: var(--color-header);
  color: var(--color-header-contrast);
}

.app-header__container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-md);
  max-width: 75rem;
  margin: 0 auto;
  padding: var(--spacing-md) var(--spacing-lg);
}

.app-header__brand {
  color: inherit;
  text-decoration: none;
}

.app-header__brand:focus-visible {
  outline: 2px solid var(--color-cta);
  outline-offset: 4px;
  border-radius: var(--radius-sm);
}

.app-header__title {
  margin: 0;
  font-size: 1.25rem;
  font-weight: 700;
}
</style>
