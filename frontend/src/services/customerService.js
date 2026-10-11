import httpClient from './httpClient'

export default {
  async getCustomerById(id) {
    const { data } = await httpClient.get(`/customers/${id}`)
    return data
  },
}
