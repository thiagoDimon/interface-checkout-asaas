export const ORDER_STATUS = {
  PENDING: {
    label: 'Pendente',
    badgeVariant: 'warning',
    alertVariant: 'warning',
    message:
      'Aguardando a confirmação do pagamento pelo Asaas. Esta página é atualizada automaticamente.',
  },
  PAID: {
    label: 'Pago',
    badgeVariant: 'success',
    alertVariant: 'success',
    message: 'Pagamento confirmado pelo Asaas. Seu pedido foi liberado.',
  },
  EXPIRED: {
    label: 'Expirado',
    badgeVariant: 'danger',
    alertVariant: 'error',
    message: 'O prazo para pagamento expirou. Nenhuma cobrança foi realizada.',
  },
  CANCELED: {
    label: 'Cancelado',
    badgeVariant: 'neutral',
    alertVariant: 'info',
    message: 'O pagamento foi cancelado.',
  },
}
