import httpClient from './httpClient'

export default {
  async createOrder({ productId, customerId, billingType, chargeType }) {
    const { data } = await httpClient.post('/orders', {
      productId,
      customerId,
      billingType,
      chargeType,
    })
    return data
  },
  async getOrdersByCustomerId(customerId) {
    const { data } = await httpClient.get('/orders', { params: { customerId } })
    return data
  },
  async getOrderById(id) {
    const { data } = await httpClient.get(`/orders/${id}`)
    return data
  },
}
