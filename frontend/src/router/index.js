import { createRouter, createWebHistory } from 'vue-router'

import CatalogView from '@/views/CatalogView.vue'
import OrderStatusView from '@/views/OrderStatusView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'catalog',
      component: CatalogView,
    },
    {
      path: '/orders/:id',
      name: 'order-status',
      component: OrderStatusView,
      props: true,
    },
  ],
})

export default router
