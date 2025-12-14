import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import http from '../api/http'

export const useClientStore = defineStore('client', () => {
  const shops = ref([])
  const dishes = ref([])
  const orders = ref([])
  const carousels = ref([])

  const loadingShops = ref(false)
  const loadingDishes = ref(false)
  const loadingOrders = ref(false)
  const loadingCarousels = ref(false)

  const selectedShopId = ref(localStorage.getItem('selectedShopId') || '')

  const currentShop = computed(() =>
    shops.value.find((shop) => String(shop.id) === String(selectedShopId.value))
  )

  const setSelectedShop = (id) => {
    selectedShopId.value = id ? String(id) : ''
    localStorage.setItem('selectedShopId', selectedShopId.value)
  }

  const loadShops = async () => {
    loadingShops.value = true
    try {
      const data = await http.get('/api/shops')
      shops.value = (data || []).filter((s) => s.status === 'APPROVED' && s.online)
      if (!selectedShopId.value && shops.value.length) {
        setSelectedShop(shops.value[0].id)
      }
    } finally {
      loadingShops.value = false
    }
  }

  const loadDishes = async () => {
    loadingDishes.value = true
    try {
      const data = await http.get('/api/dishes')
      dishes.value = (data || []).filter(
        (d) => d.available && d.shop?.status === 'APPROVED' && d.shop?.online
      )
    } finally {
      loadingDishes.value = false
    }
  }

  const loadOrders = async () => {
    loadingOrders.value = true
    try {
      orders.value = await http.get('/api/orders/my')
    } finally {
      loadingOrders.value = false
    }
  }

  const placeOrder = async (shopId, totalAmount) => {
    return http.post('/api/orders', { totalAmount }, { params: { shopId } })
  }

  const loadCarousels = async () => {
    loadingCarousels.value = true
    try {
      carousels.value = await http.get('/api/carousels/public')
    } finally {
      loadingCarousels.value = false
    }
  }

  return {
    shops,
    dishes,
    orders,
    carousels,
    loadingShops,
    loadingDishes,
    loadingOrders,
    loadingCarousels,
    selectedShopId,
    currentShop,
    setSelectedShop,
    loadShops,
    loadDishes,
    loadOrders,
    loadCarousels,
    placeOrder
  }
})
