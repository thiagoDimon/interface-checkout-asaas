import OrderStatusEnum from '@/enums/orderStatus'

export const ORDER_STATUS = Object.freeze({
  [OrderStatusEnum.PENDING]: {
    label: 'Pendente',
    badgeVariant: 'warning',
    alertVariant: 'warning',
    message: 'Aguardando a confirmação do pagamento. Esta página é atualizada automaticamente.',
  },
  [OrderStatusEnum.PAID]: {
    label: 'Pago',
    badgeVariant: 'success',
    alertVariant: 'success',
    message: 'Pagamento confirmado. Seu pedido foi liberado.',
  },
  [OrderStatusEnum.EXPIRED]: {
    label: 'Expirado',
    badgeVariant: 'danger',
    alertVariant: 'error',
    message: 'O prazo para pagamento expirou. Nenhuma cobrança foi realizada.',
  },
  [OrderStatusEnum.CANCELED]: {
    label: 'Cancelado',
    badgeVariant: 'neutral',
    alertVariant: 'info',
    message: 'O seu pedido foi cancelado.',
  },
})
