import { BILLING_TYPE_OPTIONS, CHARGE_TYPE_OPTIONS } from '@/constants/paymentOptions'
import { findOptionLabel } from '@/utils/options'

const currencyFormatter = new Intl.NumberFormat('pt-BR', {
  style: 'currency',
  currency: 'BRL',
})

export function formatCurrency(value) {
  return currencyFormatter.format(value)
}

const dateTimeFormatter = new Intl.DateTimeFormat('pt-BR', {
  dateStyle: 'short',
  timeStyle: 'short',
})

export function formatDateTime(value) {
  return dateTimeFormatter.format(new Date(value))
}

export function formatPaymentMethod(billingType, chargeType) {
  const billingTypeLabel = findOptionLabel(BILLING_TYPE_OPTIONS, billingType)
  const chargeTypeLabel = findOptionLabel(CHARGE_TYPE_OPTIONS, chargeType)
  return `${billingTypeLabel} · ${chargeTypeLabel}`
}
