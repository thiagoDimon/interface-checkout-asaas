import { defineStore } from 'pinia'
import orderService from '@/services/orderService'

export const useOrderStore = defineStore('order', {
  state: () => ({
    currentOrder: null,
    creatingProductId: null,
    loading: false,
    error: null,
  }),
  getters: {
    isCreatingOrder: (state) => state.creatingProductId !== null,
  },
  actions: {
    async createOrder({ productId, customerId }) {
      if (this.isCreatingOrder) {
        return null
      }

      this.creatingProductId = productId
      this.error = null

      try {
        this.currentOrder = await orderService.createOrder({ productId, customerId })
        return this.currentOrder
      } catch (error) {
        this.error = error.message
        return null
      } finally {
        this.creatingProductId = null
      }
    },

    async fetchOrderById(id) {
      this.loading = true
      this.error = null

      try {
        this.currentOrder = await orderService.getOrderById(id)
      } catch (error) {
        this.error = error.message
      } finally {
        this.loading = false
      }
    },

    clearError() {
      this.error = null
    },
  },
})
