<template>
  <div :id="id" class="customer-summary">
    <span :id="`${id}-avatar`" class="customer-summary__avatar" aria-hidden="true">
      {{ initials }}
    </span>
    <div class="customer-summary__info">
      <span :id="`${id}-name`" class="customer-summary__name">{{ customer.name }}</span>
      <span :id="`${id}-email`" class="customer-summary__email">{{ customer.email }}</span>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CustomerSummary',
  props: {
    id: {
      type: String,
      required: true,
    },
    customer: {
      type: Object,
      required: true,
    },
  },
  computed: {
    initials() {
      const names = this.customer.name.trim().split(/\s+/)
      const firstInitial = names[0].charAt(0)
      const lastInitial = names.length > 1 ? names[names.length - 1].charAt(0) : ''
      return `${firstInitial}${lastInitial}`.toUpperCase()
    },
  },
}
</script>

<style scoped>
.customer-summary {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.customer-summary__avatar {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 2.25rem;
  height: 2.25rem;
  border-radius: var(--radius-full);
  background-color: var(--color-cta);
  color: var(--color-cta-contrast);
  font-size: 0.875rem;
  font-weight: 700;
}

.customer-summary__info {
  display: flex;
  flex-direction: column;
  line-height: 1.25;
}

.customer-summary__name {
  font-size: 0.875rem;
  font-weight: 600;
}

.customer-summary__email {
  color: var(--color-header-muted);
  font-size: 0.75rem;
}

@media (max-width: 40rem) {
  .customer-summary__info {
    display: none;
  }
}
</style>
