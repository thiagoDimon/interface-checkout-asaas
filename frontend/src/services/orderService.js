import httpClient from './httpClient'

export default {
  async createOrder({ productId, customerId }) {
    const { data } = await httpClient.post('/orders', { productId, customerId })
    return data
  },
  async getOrderById(id) {
    const { data } = await httpClient.get(`/orders/${id}`)
    return data
  },
}
