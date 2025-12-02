<template>
  <section class="client-card glass-card">
    <header class="card-header">
      <div>
        <h2>菜品浏览</h2>
        <p class="muted">选择商家后查看菜品列表</p>
      </div>
      <div class="actions">
        <el-select
          v-model="shopSelection"
          placeholder="选择商家"
          size="small"
          style="min-width: 200px"
          @change="onShopChange"
        >
          <el-option v-for="shop in client.shops" :key="shop.id" :label="shop.name" :value="String(shop.id)" />
        </el-select>
        <el-button size="small" class="btn-soft" @click="refresh" :loading="loading">
          刷新
        </el-button>
      </div>
    </header>

    <div v-if="loading" class="placeholder">加载中...</div>
    <div v-else-if="!shopSelection" class="placeholder">请选择商家</div>
    <div v-else-if="filteredDishes.length === 0" class="placeholder">暂无菜品</div>
    <div v-else>
      <div class="page-grid">
        <div v-for="dish in filteredDishes" :key="dish.id" class="dish-card glass-card">
          <div class="dish-head">
            <div>
              <div class="dish-name">{{ dish.name }}</div>
              <small class="muted">{{ dish.category || '未分类' }}</small>
            </div>
            <el-tag :type="dish.available ? 'success' : 'info'" size="small">
              {{ dish.available ? '可售' : '售罄' }}
            </el-tag>
          </div>
          <div class="dish-price">￥{{ Number(dish.price).toFixed(2) }}</div>
          <div class="muted">{{ dish.shop?.name || '未关联商家' }}</div>
          <el-button
            :disabled="!dish.available"
            type="primary"
            size="small"
            class="btn-primary"
            style="margin-top: 8px"
            @click="addToCart(dish)"
          >
            加入下单
          </el-button>
        </div>
      </div>

      <div class="cart glass-card" v-if="cartItems.length">
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
              <small class="muted">￥{{ Number(item.price).toFixed(2) }}</small>
            </div>
            <div class="qty">
              <el-button size="small" @click="decrease(item)">-</el-button>
              <span>{{ item.quantity }}</span>
              <el-button size="small" @click="increase(item)">+</el-button>
            </div>
          </div>
        </div>
        <div class="cart-footer">
          <div class="total">合计：￥{{ totalPrice.toFixed(2) }}</div>
          <el-button type="primary" class="btn-primary" :loading="placing" @click="submitOrder">提交订单</el-button>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useClientStore } from '../store/client'

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

const onShopChange = () => {
  // trigger computed to update; nothing else needed
}

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
.client-card {
  padding: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.card-header h2 {
  margin: 0 0 4px;
}

.muted {
  margin: 0;
  color: var(--text-muted);
}

.actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.placeholder {
  padding: 24px;
  text-align: center;
  color: var(--text-muted);
}

.dish-card {
  padding: 14px;
}

.dish-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 8px;
}

.dish-name {
  font-weight: 600;
  color: var(--text-primary);
}

.dish-price {
  font-size: 18px;
  font-weight: 700;
  color: #d97706;
  margin: 10px 0 6px;
}

.cart {
  margin-top: 16px;
  padding: 14px;
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
  border: 1px solid rgba(236, 155, 52, 0.2);
  border-radius: 10px;
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
  color: #d97706;
}
</style>
