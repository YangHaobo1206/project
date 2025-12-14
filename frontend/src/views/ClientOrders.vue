<template>
  <PageContainer title="我的订单" subtitle="查看近期订单及状态">
    <template #actions>
      <el-button size="small" class="btn-soft" :loading="loading" @click="refresh">刷新</el-button>
    </template>

    <div v-if="loading" class="placeholder">加载中...</div>
    <div v-else-if="client.orders.length === 0" class="placeholder">暂无订单</div>
    <el-table
      v-else
      :data="client.orders"
      class="page-table"
      border
    >
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="商家">
        <template #default="scope">
          {{ scope.row.shop?.name || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="金额" width="140">
        <template #default="scope">
          ¥{{ Number(scope.row.totalAmount || 0).toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column label="状态" width="220">
        <template #default="scope">
          <el-tag :type="statusType(scope.row.status)">{{ statusText(scope.row.status) }}</el-tag>
          <el-button
            v-if="scope.row.status === 'CREATED'"
            size="small"
            type="primary"
            link
            @click="pay(scope.row)"
          >
            去支付
          </el-button>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="时间" />
    </el-table>
  </PageContainer>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import PageContainer from '../components/PageContainer.vue'
import { useClientStore } from '../store/client'
import http from '../api/http'

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

const pay = async (row) => {
  try {
    await http.post(`/api/orders/${row.id}/pay`)
    ElMessage.success('支付成功')
    await client.loadOrders()
  } catch (e) {
    ElMessage.error(e?.message || '支付失败')
  }
}

onMounted(async () => {
  if (!client.orders.length) {
    await client.loadOrders()
  }
})
</script>

<style scoped>
.placeholder {
  padding: 24px;
  text-align: center;
  color: var(--text-muted);
}

.page-table :deep(.el-table__cell) {
  padding: 10px 8px;
}
</style>
