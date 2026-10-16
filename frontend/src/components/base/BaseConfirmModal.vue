<template>
  <BaseModal :id="id" :open="open" :title="title" @close="handleClose">
    <p :id="`${id}-message`" class="base-confirm-modal__message">
      <slot />
    </p>

    <template #footer>
      <div class="base-confirm-modal__footer">
        <BaseAlert v-if="errorMessage" :id="`${id}-error`" variant="error">
          {{ errorMessage }}
        </BaseAlert>

        <div class="base-confirm-modal__actions">
          <BaseButton
            :id="`${id}-cancel-button`"
            variant="secondary"
            :disabled="loading"
            @click="handleClose"
          >
            {{ cancelLabel }}
          </BaseButton>
          <BaseButton
            :id="`${id}-confirm-button`"
            :variant="confirmVariant"
            :loading="loading"
            @click="handleConfirm"
          >
            {{ confirmLabel }}
          </BaseButton>
        </div>
      </div>
    </template>
  </BaseModal>
</template>

<script>
import BaseAlert from '@/components/base/BaseAlert.vue'
import BaseButton from '@/components/base/BaseButton.vue'
import BaseModal from '@/components/base/BaseModal.vue'

export default {
  name: 'BaseConfirmModal',
  components: {
    BaseAlert,
    BaseButton,
    BaseModal,
  },
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
    confirmLabel: {
      type: String,
      default: 'Confirmar',
    },
    cancelLabel: {
      type: String,
      default: 'Voltar',
    },
    confirmVariant: {
      type: String,
      default: 'primary',
    },
    loading: {
      type: Boolean,
      default: false,
    },
    errorMessage: {
      type: String,
      default: '',
    },
  },
  emits: ['close', 'confirm'],
  methods: {
    handleClose() {
      if (!this.loading) {
        this.$emit('close')
      }
    },
    handleConfirm() {
      this.$emit('confirm')
    },
  },
}
</script>

<style scoped>
.base-confirm-modal__message {
  margin: 0;
  line-height: 1.6;
}

.base-confirm-modal__footer {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.base-confirm-modal__actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: var(--spacing-sm);
}
</style>
