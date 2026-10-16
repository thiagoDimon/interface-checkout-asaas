<template>
  <button
    :id="id"
    :type="type"
    class="base-button"
    :class="[`base-button--${variant}`, { 'base-button--block': block }]"
    :disabled="isDisabled"
    :aria-busy="loading"
    @click="handleClick"
  >
    <BaseLoader v-if="loading" :id="`${id}-loader`" size="small" />
    <span class="base-button__content">
      <slot />
    </span>
  </button>
</template>

<script>
import BaseLoader from './BaseLoader.vue'

const VARIANTS = ['primary', 'secondary', 'cta', 'danger']
const TYPES = ['button', 'submit', 'reset']

export default {
  name: 'BaseButton',
  components: {
    BaseLoader,
  },
  props: {
    id: {
      type: String,
      required: true,
    },
    variant: {
      type: String,
      default: 'primary',
      validator: (value) => VARIANTS.includes(value),
    },
    type: {
      type: String,
      default: 'button',
      validator: (value) => TYPES.includes(value),
    },
    disabled: {
      type: Boolean,
      default: false,
    },
    loading: {
      type: Boolean,
      default: false,
    },
    block: {
      type: Boolean,
      default: false,
    },
  },
  emits: ['click'],
  computed: {
    isDisabled() {
      return this.disabled || this.loading
    },
  },
  methods: {
    handleClick(event) {
      if (!this.isDisabled) {
        this.$emit('click', event)
      }
    },
  },
}
</script>

<style scoped>
.base-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
  min-height: var(--control-height);
  padding: 0 1.25rem;
  border: 1px solid transparent;
  border-radius: var(--radius-full);
  font: inherit;
  font-weight: 600;
  cursor: pointer;
  transition:
    background-color var(--transition-fast),
    border-color var(--transition-fast),
    color var(--transition-fast);
}

.base-button:focus-visible {
  outline: 2px solid var(--color-primary);
  outline-offset: 2px;
}

.base-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.base-button--primary {
  background-color: var(--color-primary);
  color: var(--color-primary-contrast);
}

.base-button--primary:not(:disabled):hover {
  background-color: var(--color-primary-hover);
}

.base-button--secondary {
  background-color: var(--color-surface);
  border-color: var(--color-border);
  color: var(--color-text);
}

.base-button--secondary:not(:disabled):hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
}

.base-button--cta {
  background-color: var(--color-cta);
  color: var(--color-cta-contrast);
}

.base-button--cta:not(:disabled):hover {
  background-color: var(--color-cta-hover);
}

.base-button--danger {
  background-color: var(--color-danger);
  color: var(--color-primary-contrast);
}

.base-button--danger:not(:disabled):hover {
  background-color: var(--color-danger-hover);
}

.base-button--block {
  width: 100%;
}
</style>
