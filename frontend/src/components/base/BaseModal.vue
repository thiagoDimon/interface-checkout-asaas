<template>
  <Teleport to="body">
    <Transition name="base-modal">
      <div v-if="open" :id="`${id}-backdrop`" class="base-modal__backdrop" @click.self="close">
        <div
          :id="id"
          ref="dialog"
          class="base-modal"
          :class="`base-modal--${size}`"
          role="dialog"
          aria-modal="true"
          :aria-labelledby="`${id}-title`"
          tabindex="-1"
          @keydown.tab="trapFocus"
        >
          <header class="base-modal__header">
            <h2 :id="`${id}-title`" class="base-modal__title">{{ title }}</h2>
            <button
              :id="`${id}-close-button`"
              type="button"
              class="base-modal__close"
              aria-label="Fechar"
              @click="close"
            >
              <svg
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                aria-hidden="true"
              >
                <path d="M6 6l12 12M18 6L6 18" />
              </svg>
            </button>
          </header>

          <div class="base-modal__body">
            <slot />
          </div>

          <footer v-if="$slots.footer" class="base-modal__footer">
            <slot name="footer" />
          </footer>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script>
const SIZES = ['medium', 'large']
const FOCUSABLE_SELECTOR =
  'a[href], button:not([disabled]), input:not([disabled]), select:not([disabled]), textarea:not([disabled]), [tabindex]:not([tabindex="-1"])'

export default {
  name: 'BaseModal',
  props: {
    id: {
      type: String,
      required: true,
    },
    open: {
      type: Boolean,
      default: false,
    },
    title: {
      type: String,
      required: true,
    },
    size: {
      type: String,
      default: 'medium',
      validator: (value) => SIZES.includes(value),
    },
  },
  emits: ['close'],
  data() {
    return {
      previouslyFocusedElement: null,
    }
  },
  watch: {
    open: {
      immediate: true,
      handler(isOpen) {
        if (isOpen) {
          this.activate()
        } else {
          this.deactivate()
        }
      },
    },
  },
  beforeUnmount() {
    this.deactivate()
  },
  methods: {
    close() {
      this.$emit('close')
    },

    async activate() {
      this.previouslyFocusedElement = document.activeElement
      document.body.style.overflow = 'hidden'
      document.addEventListener('keydown', this.handleEscape)

      await this.$nextTick()
      this.$refs.dialog?.focus()
    },

    deactivate() {
      document.body.style.overflow = ''
      document.removeEventListener('keydown', this.handleEscape)

      this.previouslyFocusedElement?.focus()
      this.previouslyFocusedElement = null
    },

    handleEscape(event) {
      if (event.key === 'Escape') {
        this.close()
      }
    },

    trapFocus(event) {
      const focusableElements = [...this.$refs.dialog.querySelectorAll(FOCUSABLE_SELECTOR)]
      if (focusableElements.length === 0) {
        event.preventDefault()
        return
      }

      const firstElement = focusableElements[0]
      const lastElement = focusableElements[focusableElements.length - 1]
      const isLeavingStart = event.shiftKey && document.activeElement === firstElement
      const isLeavingEnd = !event.shiftKey && document.activeElement === lastElement
      const isOutside = !focusableElements.includes(document.activeElement)

      if (isLeavingStart || isOutside) {
        event.preventDefault()
        ;(event.shiftKey ? lastElement : firstElement).focus()
      } else if (isLeavingEnd) {
        event.preventDefault()
        firstElement.focus()
      }
    },
  },
}
</script>

<style scoped>
.base-modal__backdrop {
  position: fixed;
  inset: 0;
  z-index: var(--z-index-modal);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-md);
  background-color: var(--color-overlay);
}

.base-modal {
  display: flex;
  flex-direction: column;
  width: 100%;
  max-height: calc(100vh - 2 * var(--spacing-md));
  overflow: hidden;
  background-color: var(--color-surface);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-modal);
}

.base-modal:focus {
  outline: none;
}

.base-modal--medium {
  max-width: 32rem;
}

.base-modal--large {
  max-width: 56rem;
}

.base-modal__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-md);
  padding: var(--spacing-md) var(--spacing-lg);
  border-bottom: 1px solid var(--color-border);
}

.base-modal__title {
  margin: 0;
  font-size: 1.25rem;
  font-weight: 700;
  line-height: 1.4;
}

.base-modal__close {
  display: inline-flex;
  flex-shrink: 0;
  align-items: center;
  justify-content: center;
  width: 2.25rem;
  height: 2.25rem;
  padding: 0;
  border: none;
  border-radius: var(--radius-full);
  background-color: transparent;
  color: var(--color-text-muted);
  cursor: pointer;
  transition:
    background-color var(--transition-fast),
    color var(--transition-fast);
}

.base-modal__close:hover {
  background-color: var(--color-neutral-soft);
  color: var(--color-text);
}

.base-modal__close:focus-visible {
  outline: 2px solid var(--color-primary);
  outline-offset: 2px;
}

.base-modal__close svg {
  width: 1.25rem;
  height: 1.25rem;
}

.base-modal__body {
  overflow-y: auto;
  padding: var(--spacing-lg);
}

.base-modal__footer {
  padding: var(--spacing-md) var(--spacing-lg);
  border-top: 1px solid var(--color-border);
}

.base-modal-enter-active,
.base-modal-leave-active {
  transition: opacity var(--transition-fast);
}

.base-modal-enter-active .base-modal,
.base-modal-leave-active .base-modal {
  transition: transform var(--transition-fast);
}

.base-modal-enter-from,
.base-modal-leave-to {
  opacity: 0;
}

.base-modal-enter-from .base-modal,
.base-modal-leave-to .base-modal {
  transform: translateY(1rem) scale(0.98);
}
</style>
