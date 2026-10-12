<template>
  <fieldset
    :id="id"
    class="base-radio-group"
    :class="{ 'base-radio-group--disabled': disabled }"
    :disabled="disabled"
  >
    <legend :id="`${id}-legend`" class="base-radio-group__legend">{{ legend }}</legend>

    <label
      v-for="option in options"
      :id="`${id}-option-${option.value}`"
      :key="option.value"
      class="base-radio-group__option"
      :class="{ 'base-radio-group__option--selected': option.value === modelValue }"
    >
      <input
        :id="`${id}-input-${option.value}`"
        class="base-radio-group__input"
        type="radio"
        :name="id"
        :value="option.value"
        :checked="option.value === modelValue"
        @change="handleChange(option.value)"
      />
      <span class="base-radio-group__content">
        <span class="base-radio-group__label">{{ option.label }}</span>
        <span v-if="option.description" class="base-radio-group__description">
          {{ option.description }}
        </span>
      </span>
    </label>
  </fieldset>
</template>

<script>
export default {
  name: 'BaseRadioGroup',
  props: {
    id: {
      type: String,
      required: true,
    },
    legend: {
      type: String,
      required: true,
    },
    options: {
      type: Array,
      required: true,
    },
    modelValue: {
      type: String,
      default: null,
    },
    disabled: {
      type: Boolean,
      default: false,
    },
  },
  emits: ['update:modelValue'],
  methods: {
    handleChange(value) {
      this.$emit('update:modelValue', value)
    },
  },
}
</script>

<style scoped>
.base-radio-group {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(12rem, 1fr));
  gap: var(--spacing-sm);
  margin: 0;
  padding: 0;
  border: none;
}

.base-radio-group__legend {
  margin-bottom: var(--spacing-sm);
  padding: 0;
  color: var(--color-text-muted);
  font-size: 0.75rem;
  font-weight: 600;
  letter-spacing: 0.04em;
  text-transform: uppercase;
}

.base-radio-group__option {
  display: flex;
  align-items: flex-start;
  gap: var(--spacing-sm);
  padding: var(--spacing-md);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  background-color: var(--color-surface);
  cursor: pointer;
  transition: border-color var(--transition-fast);
}

.base-radio-group:not(.base-radio-group--disabled) .base-radio-group__option:hover,
.base-radio-group__option--selected {
  border-color: var(--color-primary);
}

.base-radio-group--disabled .base-radio-group__option {
  opacity: 0.6;
  cursor: not-allowed;
}

.base-radio-group__option:focus-within {
  outline: 2px solid var(--color-primary);
  outline-offset: 2px;
}

.base-radio-group__input {
  margin: 0.2rem 0 0;
  accent-color: var(--color-primary);
}

.base-radio-group__content {
  display: flex;
  flex-direction: column;
}

.base-radio-group__label {
  font-weight: 600;
}

.base-radio-group__description {
  color: var(--color-text-muted);
  font-size: 0.875rem;
}
</style>
