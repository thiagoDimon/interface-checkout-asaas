import App from '@/App.vue'
import { createPinia } from 'pinia'
import { createRouter, createMemoryHistory } from 'vue-router'
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { ORDER_STATUS } from '@/constants/orderStatus'
import BillingTypeEnum from '@/enums/billingType'
import ChargeTypeEnum from '@/enums/chargeType'
import OrderStatusEnum from '@/enums/orderStatus'
import customerService from '@/services/customerService'
import orderService from '@/services/orderService'
import productService from '@/services/productService'
import CatalogView from '@/views/CatalogView.vue'
import OrderStatusView from '@/views/OrderStatusView.vue'

vi.mock('@/services/customerService', () => ({ default: { getCustomerById: vi.fn() } }))
vi.mock('@/services/productService', () => ({ default: { getAllProducts: vi.fn() } }))
vi.mock('@/services/orderService', () => ({
  default: { createOrder: vi.fn(), getOrderById: vi.fn(), getOrdersByCustomerId: vi.fn() },
}))

const POLLING_INTERVAL_MS = 5000

const ORDER_ID = 1

const CHECKOUT_URL = 'https://sandbox.asaas.com/checkoutSession/show/chk_test_123'

const CUSTOMER = {
  id: 1,
  name: 'João Machado Silva',
  email: 'joao.silva@email.com',
}

const PRODUCT = {
  id: 1,
  name: 'Camiseta Masculina Casual',
  description: 'Camiseta de algodão',
  price: 89.9,
  image: 'camiseta.png',
}

function buildOrder(status) {
  return {
    id: ORDER_ID,
    productId: PRODUCT.id,
    productName: PRODUCT.name,
    amount: PRODUCT.price,
    customerId: CUSTOMER.id,
    customerName: CUSTOMER.name,
    billingType: BillingTypeEnum.CREDIT_CARD,
    chargeType: ChargeTypeEnum.DETACHED,
    checkoutUrl: CHECKOUT_URL,
    createdAt: new Date(),
    updatedAt: new Date(),
    status,
  }
}

async function mountApp() {
  const router = createRouter({
    history: createMemoryHistory(),
    routes: [
      { path: '/', name: 'catalog', component: CatalogView },
      { path: '/orders/:id', name: 'order-status', component: OrderStatusView, props: true },
    ],
  })
  router.push('/')
  await router.isReady()

  const wrapper = mount(App, {
    global: { plugins: [createPinia(), router] },
    attachTo: document.body,
  })
  await flushPromises()

  return { wrapper, router }
}

describe('Fluxo de compra', () => {
  let wrapper

  beforeEach(() => {
    vi.clearAllMocks()
    vi.stubGlobal('location', { assign: vi.fn() })
    customerService.getCustomerById.mockResolvedValue(CUSTOMER)
    productService.getAllProducts.mockResolvedValue([PRODUCT])
    orderService.createOrder.mockResolvedValue(buildOrder(OrderStatusEnum.PENDING))
  })

  afterEach(() => {
    wrapper?.unmount()
    vi.useRealTimers()
    vi.unstubAllGlobals()
  })

  it.each([
    // Deve marcar o pedido como pago quando o pagamento for aprovado
    ['aprovado', OrderStatusEnum.PAID],
    // Deve marcar o pedido como expirado quando o tempo limite para pagamento for excedido
    ['expirado', OrderStatusEnum.EXPIRED],
    // Deve marcar o pedido como cancelado quando o pagamento for cancelado
    ['cancelado', OrderStatusEnum.CANCELED],
  ])('compra com pedido %s atualiza o status após o webhook', async (_, finalStatus) => {
    orderService.getOrderById
      .mockResolvedValueOnce(buildOrder(OrderStatusEnum.PENDING))
      .mockResolvedValue(buildOrder(finalStatus))

    const app = await mountApp()
    wrapper = app.wrapper

    await wrapper.find('#product-card-1-buy-button').trigger('click')
    await flushPromises()
    document.body.querySelector('#payment-options-modal-confirm-button').click()
    await flushPromises()

    expect(orderService.createOrder).toHaveBeenCalledWith({
      productId: PRODUCT.id,
      customerId: CUSTOMER.id,
      billingType: BillingTypeEnum.CREDIT_CARD,
      chargeType: ChargeTypeEnum.DETACHED,
    })
    expect(window.location.assign).toHaveBeenCalledWith(CHECKOUT_URL)

    vi.useFakeTimers()
    await app.router.push(`/orders/${ORDER_ID}`)
    await flushPromises()

    expect(wrapper.find('#order-status-badge').text()).toBe(ORDER_STATUS.PENDING.label)
    expect(wrapper.find('#order-status-checkout-button').exists()).toBe(true)

    await vi.advanceTimersByTimeAsync(POLLING_INTERVAL_MS)
    await flushPromises()

    expect(wrapper.find('#order-status-badge').text()).toBe(ORDER_STATUS[finalStatus].label)
    expect(wrapper.find('#order-status-message').text()).toContain(
      ORDER_STATUS[finalStatus].message,
    )
    expect(wrapper.find('#order-status-checkout-button').exists()).toBe(false)
  })
})
