import { defineStore } from 'pinia'
import orderService from '@/services/orderService'

export const useOrderStore = defineStore('order', {
  state: () => ({
    currentOrder: null,
    orders: [],
    creatingProductId: null,
    loading: false,
    error: null,
  }),
  getters: {
    isCreatingOrder: (state) => state.creatingProductId !== null,
  },
  actions: {
    async createOrder({ productId, customerId, billingType, chargeType }) {
      if (this.isCreatingOrder) {
        return null
      }

      this.creatingProductId = productId
      this.error = null

      try {
        this.currentOrder = await orderService.createOrder({
          productId,
          customerId,
          billingType,
          chargeType,
        })
        return this.currentOrder
      } catch (error) {
        this.error = error.message
        return null
      } finally {
        this.creatingProductId = null
      }
    },

    async fetchOrdersByCustomerId(customerId) {
      this.loading = true
      this.error = null

      try {
        this.orders = await orderService.getOrdersByCustomerId(customerId)
      } catch (error) {
        this.error = error.message
      } finally {
        this.loading = false
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
