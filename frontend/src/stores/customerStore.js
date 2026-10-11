import { defineStore } from 'pinia'
import customerService from '@/services/customerService'

const CURRENT_CUSTOMER_ID = import.meta.env.VITE_CUSTOMER_ID

export const useCustomerStore = defineStore('customer', {
  state: () => ({
    customer: null,
    loading: false,
    error: null,
  }),
  getters: {
    hasCustomer: (state) => state.customer !== null,
  },
  actions: {
    async fetchCurrentCustomer() {
      this.loading = true
      this.error = null

      try {
        this.customer = await customerService.getCustomerById(CURRENT_CUSTOMER_ID)
      } catch (error) {
        this.error = error.message
      } finally {
        this.loading = false
      }
    },
  },
})
