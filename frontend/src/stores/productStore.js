import { defineStore } from 'pinia'
import productService from '@/services/productService'

export const useProductStore = defineStore('product', {
  state: () => ({
    products: [],
    loading: false,
    error: null,
  }),
  getters: {
    hasProducts: (state) => state.products.length > 0,
  },
  actions: {
    async fetchProducts() {
      this.loading = true
      this.error = null

      try {
        this.products = await productService.getAllProducts()
      } catch (error) {
        this.error = error.message
      } finally {
        this.loading = false
      }
    },
  },
})
