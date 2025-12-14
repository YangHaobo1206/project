<template>
  <PageContainer title="订单管理" subtitle="查看并处理用户在本店的订单">
    <template #actions>
      <el-button size="small" class="btn-soft" :loading="loading" @click="load">刷新</el-button>
    </template>

    <el-alert
      v-if="shop && shop.status !== 'APPROVED'"
      :title="`当前店铺状态：${statusText(shop.status)}，审核通过后才能处理订单`"
      type="warning"
      show-icon
      :closable="false"
      style="margin-bottom: 10px"
    />

    <el-table :data="orders" class="page-table" :loading="loading" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="user.username" label="用户" />
      <el-table-column prop="shop.name" label="店铺" />
      <el-table-column prop="totalAmount" label="金额" />
      <el-table-column prop="status" label="状态" />
      <el-table-column prop="createdAt" label="下单时间" />
      <el-table-column label="操作" width="260">
        <template #default="scope">
          <el-button
            size="small"
            type="primary"
            :disabled="!canAccept(scope.row)"
            @click="accept(scope.row)"
          >
            接单
          </el-button>
          <el-button
            size="small"
            type="success"
            :disabled="!canComplete(scope.row)"
            @click="complete(scope.row)"
          >
            出餐完成
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </PageContainer>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import PageContainer from '../components/PageContainer.vue'
import http from '../api/http'

const orders = ref([])
const shop = ref(null)
const loading = ref(false)

const statusText = (status) => {
  if (status === 'APPROVED') return '审核通过'
  if (status === 'PENDING') return '待审核'
  if (status === 'REJECTED') return '已拒绝'
  return status || '-'
}

const load = async () => {
  loading.value = true
  try {
    const [shopList, orderList] = await Promise.all([
      http.get('/api/shops/mine'),
      http.get('/api/orders/shop/mine')
    ])
    shop.value = shopList?.[0] || null
    orders.value = orderList || []
  } finally {
    loading.value = false
  }
}

const canAccept = (row) => row.status === 'CREATED' || row.status === 'PAID'
const canComplete = (row) => row.status === 'ACCEPTED'

const accept = async (row) => {
  try {
    await http.post(`/api/orders/${row.id}/accept`)
    ElMessage.success('已接单')
    await load()
  } catch (e) {
    ElMessage.error(e?.message || '操作失败')
  }
}

const complete = async (row) => {
  try {
    await http.post(`/api/orders/${row.id}/complete`)
    ElMessage.success('已完成')
    await load()
  } catch (e) {
    ElMessage.error(e?.message || '操作失败')
  }
}

load()
</script>

<style scoped>
.page-table :deep(.el-table__cell) {
  padding: 10px 8px;
}
</style>
