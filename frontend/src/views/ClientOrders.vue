<template>
  <section class="client-card glass-card">
    <header class="card-header">
      <div>
        <h2>我的订单</h2>
        <p class="muted">查看近期订单及状态</p>
      </div>
      <el-button size="small" class="btn-soft" @click="refresh" :loading="loading">刷新</el-button>
    </header>

    <div v-if="loading" class="placeholder">加载中...</div>
    <div v-else-if="client.orders.length === 0" class="placeholder">暂无订单</div>
    <el-table
      v-else
      :data="client.orders"
      class="glass-card"
      style="width: 100%; background: transparent;"
      :header-cell-style="{ background: 'transparent' }"
    >
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="商家">
        <template #default="scope">
          {{ scope.row.shop?.name || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="金额" width="140">
        <template #default="scope">
          ￥{{ Number(scope.row.totalAmount || 0).toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column label="状态" width="140">
        <template #default="scope">
          <el-tag :type="statusType(scope.row.status)">{{ statusText(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="时间" />
    </el-table>
  </section>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useClientStore } from '../store/client'

const client = useClientStore()
const loading = computed(() => client.loadingOrders)

const refresh = () => client.loadOrders()

const statusText = (val) => {
  switch (val) {
    case 'PAID':
      return '已支付'
    case 'ACCEPTED':
      return '已接单'
    case 'COMPLETED':
      return '已完成'
    case 'CANCELLED':
      return '已取消'
    default:
      return '待支付'
  }
}

const statusType = (val) => {
  switch (val) {
    case 'PAID':
      return 'success'
    case 'ACCEPTED':
      return 'warning'
    case 'COMPLETED':
      return 'success'
    case 'CANCELLED':
      return 'info'
    default:
      return 'info'
  }
}

onMounted(async () => {
  if (!client.orders.length) {
    await client.loadOrders()
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
</style>
