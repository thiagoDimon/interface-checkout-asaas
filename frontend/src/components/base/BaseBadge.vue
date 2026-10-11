<template>
  <span :id="id" class="base-badge" :class="`base-badge--${variant}`">
    <slot />
  </span>
</template>

<script>
const VARIANTS = ['neutral', 'info', 'success', 'warning', 'danger']

export default {
  name: 'BaseBadge',
  props: {
    id: {
      type: String,
      required: true,
    },
    variant: {
      type: String,
      default: 'neutral',
      validator: (value) => VARIANTS.includes(value),
    },
  },
}
</script>

<style scoped>
.base-badge {
  display: inline-flex;
  align-items: center;
  padding: 0.125rem var(--spacing-sm);
  border-radius: var(--radius-full);
  font-size: 0.75rem;
  font-weight: 600;
  letter-spacing: 0.02em;
  text-transform: uppercase;
}

.base-badge--neutral {
  color: var(--color-neutral);
  background-color: var(--color-neutral-soft);
}

.base-badge--info {
  color: var(--color-info);
  background-color: var(--color-info-soft);
}

.base-badge--success {
  color: var(--color-success);
  background-color: var(--color-success-soft);
}

.base-badge--warning {
  color: var(--color-warning);
  background-color: var(--color-warning-soft);
}

.base-badge--danger {
  color: var(--color-danger);
  background-color: var(--color-danger-soft);
}
</style>
