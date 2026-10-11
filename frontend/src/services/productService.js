import httpClient from './httpClient'

export default {
  async getAllProducts() {
    const { data } = await httpClient.get('/products')
    return data
  },
}
