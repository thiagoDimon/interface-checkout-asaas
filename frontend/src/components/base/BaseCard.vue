<template>
  <component :is="tag" :id="id" class="base-card" :class="{ 'base-card--hoverable': hoverable }">
    <div v-if="$slots.media" class="base-card__media">
      <slot name="media" />
    </div>

    <div class="base-card__body">
      <slot />
    </div>

    <footer v-if="$slots.footer" class="base-card__footer">
      <slot name="footer" />
    </footer>
  </component>
</template>

<script>
export default {
  name: 'BaseCard',
  props: {
    id: {
      type: String,
      required: true,
    },
    tag: {
      type: String,
      default: 'article',
    },
    hoverable: {
      type: Boolean,
      default: false,
    },
  },
}
</script>

<style scoped>
.base-card {
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background-color: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-card);
}

.base-card--hoverable {
  transition:
    transform var(--transition-fast),
    box-shadow var(--transition-fast);
}

.base-card--hoverable:hover,
.base-card--hoverable:focus-within {
  transform: translateY(-4px);
  box-shadow: var(--shadow-card-hover);
}

.base-card__media {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-md);
  background-color: var(--color-surface-muted);
}

.base-card__body {
  flex: 1;
  padding: var(--spacing-md);
}

.base-card__footer {
  padding: var(--spacing-md);
  border-top: 1px solid var(--color-border);
}
</style>
