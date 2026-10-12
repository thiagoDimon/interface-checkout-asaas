<template>
  <BaseModal :id="id" :open="open" title="Forma de pagamento" @close="handleClose">
    <div v-if="product" class="payment-options">
      <div :id="`${id}-summary`" class="payment-options__summary">
        <span :id="`${id}-product-name`">{{ product.name }}</span>
        <strong :id="`${id}-product-price`" class="payment-options__price">
          {{ formattedPrice }}
        </strong>
      </div>

      <BaseRadioGroup
        :id="`${id}-billing-type`"
        v-model="billingType"
        legend="Como você quer pagar?"
        :options="billingTypeOptions"
      />

      <BaseRadioGroup
        :id="`${id}-charge-type`"
        v-model="chargeType"
        legend="Condição de pagamento"
        :options="chargeTypeOptions"
        :disabled="!isCreditCard"
      />
    </div>

    <template #footer>
      <div class="payment-options__footer">
        <BaseAlert
          v-if="errorMessage"
          :id="`${id}-error`"
          variant="error"
          title="Não foi possível iniciar o pagamento"
        >
          {{ errorMessage }}
        </BaseAlert>

        <BaseButton
          :id="`${id}-confirm-button`"
          class="payment-options__confirm-button"
          variant="cta"
          :loading="loading"
          :disabled="disabled"
          @click="handleConfirm"
        >
          Ir para o pagamento
        </BaseButton>
      </div>
    </template>
  </BaseModal>
</template>

<script>
import BaseAlert from '@/components/base/BaseAlert.vue'
import BaseButton from '@/components/base/BaseButton.vue'
import BaseModal from '@/components/base/BaseModal.vue'
import BaseRadioGroup from '@/components/base/BaseRadioGroup.vue'
import { BILLING_TYPE_OPTIONS, CHARGE_TYPE_OPTIONS } from '@/constants/paymentOptions'
import BillingTypeEnum from '@/enums/billingType'
import ChargeTypeEnum from '@/enums/chargeType'
import { formatCurrency } from '@/utils/formatters'

const DEFAULT_BILLING_TYPE = BillingTypeEnum.CREDIT_CARD
const DEFAULT_CHARGE_TYPE = ChargeTypeEnum.DETACHED

export default {
  name: 'PaymentOptionsModal',
  components: {
    BaseAlert,
    BaseButton,
    BaseModal,
    BaseRadioGroup,
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
    product: {
      type: Object,
      default: null,
    },
    loading: {
      type: Boolean,
      default: false,
    },
    disabled: {
      type: Boolean,
      default: false,
    },
    errorMessage: {
      type: String,
      default: '',
    },
  },
  emits: ['close', 'confirm'],
  data() {
    return {
      billingType: DEFAULT_BILLING_TYPE,
      chargeType: DEFAULT_CHARGE_TYPE,
      billingTypeOptions: BILLING_TYPE_OPTIONS,
      chargeTypeOptions: CHARGE_TYPE_OPTIONS,
    }
  },
  computed: {
    isCreditCard() {
      return this.billingType === BillingTypeEnum.CREDIT_CARD
    },
    formattedPrice() {
      return formatCurrency(this.product.price)
    },
  },
  watch: {
    open(isOpen) {
      if (isOpen) {
        this.billingType = DEFAULT_BILLING_TYPE
        this.chargeType = DEFAULT_CHARGE_TYPE
      }
    },
    billingType() {
      if (!this.isCreditCard) {
        this.chargeType = DEFAULT_CHARGE_TYPE
      }
    },
  },
  methods: {
    handleClose() {
      this.$emit('close')
    },
    handleConfirm() {
      this.$emit('confirm', { billingType: this.billingType, chargeType: this.chargeType })
    },
  },
}
</script>

<style scoped>
.payment-options {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.payment-options__summary {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-md);
  padding: var(--spacing-md);
  border-radius: var(--radius-sm);
  background-color: var(--color-surface-muted);
}

.payment-options__price {
  font-size: 1.25rem;
  font-variant-numeric: tabular-nums;
  white-space: nowrap;
}

.payment-options__footer {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.payment-options__confirm-button {
  align-self: flex-end;
  min-width: 12rem;
}

@media (max-width: 40rem) {
  .payment-options__confirm-button {
    align-self: stretch;
  }
}
</style>
