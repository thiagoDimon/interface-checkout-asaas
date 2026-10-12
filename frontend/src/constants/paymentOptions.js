import BillingTypeEnum from '@/enums/billingType'
import ChargeTypeEnum from '@/enums/chargeType'

export const BILLING_TYPE_OPTIONS = Object.freeze([
  {
    value: BillingTypeEnum.CREDIT_CARD,
    label: 'Cartão de crédito',
    description: 'Pagamento com cartão de crédito.',
  },
  {
    value: BillingTypeEnum.PIX,
    label: 'Pix',
    description: 'Pagamento instantâneo via QR Code.',
  },
])

export const CHARGE_TYPE_OPTIONS = Object.freeze([
  {
    value: ChargeTypeEnum.DETACHED,
    label: 'À vista',
    description: 'Pagamento em uma única parcela.',
  },
  {
    value: ChargeTypeEnum.INSTALLMENT,
    label: 'Parcelado',
    description: 'Em até 12x no cartão de crédito.',
  },
])
