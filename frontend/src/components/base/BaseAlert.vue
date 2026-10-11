<template>
  <div :id="id" class="base-alert" :class="`base-alert--${variant}`" :role="role">
    <strong v-if="title" class="base-alert__title">{{ title }}</strong>
    <div class="base-alert__message">
      <slot />
    </div>
  </div>
</template>

<script>
const VARIANTS = ['info', 'success', 'warning', 'error']

export default {
  name: 'BaseAlert',
  props: {
    id: {
      type: String,
      required: true,
    },
    variant: {
      type: String,
      default: 'info',
      validator: (value) => VARIANTS.includes(value),
    },
    title: {
      type: String,
      default: '',
    },
  },
  computed: {
    role() {
      return this.variant === 'error' ? 'alert' : 'status'
    },
  },
}
</script>

<style scoped>
.base-alert {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
  padding: var(--spacing-sm) var(--spacing-md);
  border-left: 4px solid currentColor;
  border-radius: var(--radius-sm);
}

.base-alert__message {
  color: var(--color-text);
}

.base-alert--info {
  color: var(--color-info);
  background-color: var(--color-info-soft);
}

.base-alert--success {
  color: var(--color-success);
  background-color: var(--color-success-soft);
}

.base-alert--warning {
  color: var(--color-warning);
  background-color: var(--color-warning-soft);
}

.base-alert--error {
  color: var(--color-danger);
  background-color: var(--color-danger-soft);
}
</style>
