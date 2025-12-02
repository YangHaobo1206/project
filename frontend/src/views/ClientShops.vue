<template>
  <section class="client-card glass-card">
    <header class="card-header">
      <div>
        <h2>商家列表</h2>
        <p class="muted">选择一家商家查看菜品并下单</p>
      </div>
      <el-button size="small" class="btn-soft" @click="refresh" :loading="loading">
        刷新
      </el-button>
    </header>

    <div v-if="loading" class="placeholder">加载中...</div>
    <div v-else class="page-grid">
      <div
        v-for="shop in client.shops"
        :key="shop.id"
        class="shop-card glass-card"
        :class="{ active: isActive(shop.id) }"
        @click="select(shop.id)"
      >
        <div class="shop-head">
          <div class="shop-name">{{ shop.name }}</div>
          <el-tag :type="shop.online ? 'success' : 'info'" size="small">
            {{ shop.online ? '营业中' : '休息' }}
          </el-tag>
        </div>
        <p class="muted ellipsis">{{ shop.description || '暂无简介' }}</p>
        <p class="muted ellipsis">地址：{{ shop.address || '未填写' }}</p>
        <div class="shop-actions">
          <el-button type="primary" size="small" class="btn-primary" @click.stop="goDishes(shop.id)">
            查看菜品
          </el-button>
          <span v-if="isActive(shop.id)" class="current">当前</span>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useClientStore } from '../store/client'
import { useRouter } from 'vue-router'

const client = useClientStore()
const router = useRouter()
const loading = computed(() => client.loadingShops)

const isActive = (id) => String(id) === String(client.selectedShopId)
const select = (id) => client.setSelectedShop(id)
const refresh = () => client.loadShops()
const goDishes = (id) => {
  select(id)
  router.push('/client/dishes')
}

onMounted(async () => {
  if (!client.shops.length) {
    await client.loadShops()
  }
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

.placeholder {
  padding: 24px;
  text-align: center;
  color: var(--text-muted);
}

.shop-card {
  padding: 14px;
  border: 1px solid transparent;
  cursor: pointer;
  transition: transform 0.18s ease, border-color 0.18s ease;
}

.shop-card:hover {
  transform: translateY(-2px);
  border-color: rgba(236, 155, 52, 0.4);
}

.shop-card.active {
  border-color: var(--card-border);
  box-shadow: 0 12px 26px rgba(245, 158, 11, 0.22);
}

.shop-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}

.shop-name {
  font-weight: 600;
  color: var(--text-primary);
}

.ellipsis {
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.shop-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 10px;
}

.current {
  font-size: 12px;
  color: var(--accent);
}
</style>
