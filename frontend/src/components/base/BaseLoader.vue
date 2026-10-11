<template>
  <span :id="id" class="base-loader" :class="`base-loader--${size}`" role="status">
    <span class="base-loader__spinner" aria-hidden="true"></span>
    <span class="base-loader__label">{{ label }}</span>
  </span>
</template>

<script>
const SIZES = ['small', 'medium', 'large']

export default {
  name: 'BaseLoader',
  props: {
    id: {
      type: String,
      required: true,
    },
    size: {
      type: String,
      default: 'medium',
      validator: (value) => SIZES.includes(value),
    },
    label: {
      type: String,
      default: 'Carregando...',
    },
  },
}
</script>

<style scoped>
.base-loader {
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.base-loader__spinner {
  border: 2px solid currentColor;
  border-right-color: transparent;
  border-radius: var(--radius-full);
  animation: base-loader-spin 0.75s linear infinite;
}

.base-loader--small .base-loader__spinner {
  width: 1rem;
  height: 1rem;
}

.base-loader--medium .base-loader__spinner {
  width: 1.75rem;
  height: 1.75rem;
}

.base-loader--large .base-loader__spinner {
  width: 2.75rem;
  height: 2.75rem;
  border-width: 3px;
}

.base-loader__label {
  position: absolute;
  width: 1px;
  height: 1px;
  overflow: hidden;
  clip: rect(0 0 0 0);
  white-space: nowrap;
}

@keyframes base-loader-spin {
  to {
    transform: rotate(360deg);
  }
}
</style>
