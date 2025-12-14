<template>
  <PageContainer title="菜品浏览" subtitle="选择商家后查看可下单的菜品">
    <template #actions>
      <el-select
        v-model="shopSelection"
        placeholder="选择商家"
        size="small"
        style="min-width: 200px"
        @change="onShopChange"
      >
        <el-option v-for="shop in client.shops" :key="shop.id" :label="shop.name" :value="String(shop.id)" />
      </el-select>
      <el-button size="small" class="btn-soft" @click="refresh" :loading="loading">刷新</el-button>
    </template>

    <div v-if="loading" class="placeholder">加载中...</div>
    <div v-else-if="!shopSelection" class="placeholder">请选择商家</div>
    <div v-else-if="filteredDishes.length === 0" class="placeholder">暂无菜品</div>
    <div v-else class="dishes-layout">
      <div class="dish-grid">
        <DishCard
          v-for="dish in filteredDishes"
          :key="dish.id"
          :dish="dish"
          :api-base="apiBase"
        >
          <template #default>
            <el-button
              :disabled="!dish.available"
              type="primary"
              size="small"
              class="btn-primary"
              @click="addToCart(dish)"
            >
              加入下单
            </el-button>
          </template>
        </DishCard>
      </div>

      <div class="cart-panel" v-if="cartItems.length">
        <div class="cart-head">
          <div>
            <h3>下单清单</h3>
            <p class="muted">确认后提交订单</p>
          </div>
          <el-button size="small" class="btn-soft" @click="clearCart">清空</el-button>
        </div>
        <div class="cart-list">
          <div v-for="item in cartItems" :key="item.id" class="cart-row">
            <div>
              <div class="dish-name">{{ item.name }}</div>
              <small class="muted">¥{{ Number(item.price).toFixed(2) }}</small>
            </div>
            <div class="qty">
              <el-button size="small" @click="decrease(item)">-</el-button>
              <span>{{ item.quantity }}</span>
              <el-button size="small" @click="increase(item)">+</el-button>
            </div>
          </div>
        </div>
        <div class="cart-footer">
          <div class="total">合计：¥{{ totalPrice.toFixed(2) }}</div>
          <el-button type="primary" class="btn-primary" :loading="placing" @click="submitOrder">提交订单</el-button>
        </div>
      </div>
    </div>
  </PageContainer>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import PageContainer from '../components/PageContainer.vue'
import DishCard from '../components/DishCard.vue'
import { useClientStore } from '../store/client'

const apiBase = import.meta.env.VITE_API_BASE || 'http://localhost:8080'
const client = useClientStore()
const placing = ref(false)
const cart = reactive({})

const shopSelection = computed({
  get: () => client.selectedShopId,
  set: (val) => client.setSelectedShop(val)
})

const loading = computed(() => client.loadingDishes || client.loadingShops)

const filteredDishes = computed(() => {
  if (!shopSelection.value) return []
  return client.dishes.filter((dish) => String(dish.shop?.id) === String(shopSelection.value))
})

const cartItems = computed(() =>
  Object.values(cart).map((item) => ({
    ...item,
    quantity: item.quantity || 1
  }))
)

const totalPrice = computed(() =>
  cartItems.value.reduce((sum, item) => sum + Number(item.price || 0) * item.quantity, 0)
)

const refresh = async () => {
  await Promise.all([client.loadShops(), client.loadDishes()])
}

const onShopChange = () => {}

const addToCart = (dish) => {
  const key = dish.id
  if (!cart[key]) {
    cart[key] = { ...dish, quantity: 1 }
  } else {
    cart[key].quantity += 1
  }
}

const increase = (item) => {
  cart[item.id].quantity += 1
}

const decrease = (item) => {
  if (cart[item.id].quantity <= 1) {
    delete cart[item.id]
  } else {
    cart[item.id].quantity -= 1
  }
}

const clearCart = () => {
  Object.keys(cart).forEach((k) => delete cart[k])
}

const submitOrder = async () => {
  if (!shopSelection.value) {
    ElMessage.warning('请先选择商家')
    return
  }
  if (!cartItems.value.length) {
    ElMessage.warning('请选择菜品')
    return
  }
  placing.value = true
  try {
    await client.placeOrder(shopSelection.value, totalPrice.value)
    ElMessage.success('下单成功')
    clearCart()
    await client.loadOrders()
  } catch (error) {
    ElMessage.error(error?.message || '下单失败')
  } finally {
    placing.value = false
  }
}

onMounted(async () => {
  if (!client.shops.length) {
    await client.loadShops()
  }
  if (!client.dishes.length) {
    await client.loadDishes()
  }
  await client.loadOrders()
})
</script>

<style scoped>
.placeholder {
  padding: 24px;
  text-align: center;
  color: var(--text-muted);
}

.dishes-layout {
  display: grid;
  gap: 16px;
}

.dish-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 14px;
}

.cart-panel {
  border: 1px solid var(--card-border);
  border-radius: var(--radius-md);
  padding: 14px;
  background: var(--card-bg);
}

.cart-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.cart-list {
  display: grid;
  gap: 10px;
}

.cart-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  border: 1px solid var(--card-border);
  border-radius: var(--radius-md);
}

.qty {
  display: flex;
  align-items: center;
  gap: 8px;
}

.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
}

.total {
  font-weight: 700;
  color: #0f172a;
}
</style>
