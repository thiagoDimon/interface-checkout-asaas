<template>
  <div id="app-layout">
    <AppHeader id="app-header" />

    <main id="app-content">
      <RouterView />
    </main>
  </div>
</template>

<script>
import { mapActions } from 'pinia'
import AppHeader from '@/components/layout/AppHeader.vue'
import { useCustomerStore } from '@/stores/customerStore'

export default {
  name: 'App',
  components: {
    AppHeader,
  },
  async created() {
    await this.fetchCurrentCustomer()
  },
  methods: {
    ...mapActions(useCustomerStore, ['fetchCurrentCustomer']),
  },
}
</script>

<style scoped>
#app-content {
  max-width: 75rem;
  margin: 0 auto;
  padding: var(--spacing-xl) var(--spacing-lg);
}
</style>
