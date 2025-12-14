<template>
  <PageContainer title="订单管理" subtitle="支付、接单、完成、取消">
    <template #actions>
      <el-button size="small" class="btn-soft" @click="load">刷新</el-button>
    </template>
    <el-table :data="orders" class="page-table" :loading="loading" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="用户">
        <template #default="scope">
          {{ scope.row.user?.username || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="商家">
        <template #default="scope">
          {{ scope.row.shop?.name || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="totalAmount" label="总价" />
      <el-table-column prop="status" label="状态" />
      <el-table-column prop="createdAt" label="创建时间" />
      <el-table-column label="操作" width="260">
        <template #default="scope">
          <el-button size="small" @click="cancel(scope.row)" :disabled="scope.row.status === 'CANCELLED'">
            取消
          </el-button>
          <el-button size="small" type="danger" @click="remove(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </PageContainer>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import PageContainer from '../components/PageContainer.vue'
import http from '../api/http'

const orders = ref([])
const loading = ref(false)

const load = async () => {
  loading.value = true
  try {
    orders.value = await http.get('/api/orders')
  } finally {
    loading.value = false
  }
}

const cancel = async (row) => {
  await http.post(`/api/orders/${row.id}/cancel`)
  ElMessage.success('已取消')
  await load()
}

const remove = async (row) => {
  await ElMessageBox.confirm('确定删除该订单吗？', '提示', { type: 'warning' })
  await http.delete(`/api/orders/${row.id}`)
  ElMessage.success('删除成功')
  await load()
}

onMounted(load)
</script>

<style scoped>
.page-table :deep(.el-table__cell) {
  padding: 10px 8px;
}
</style>
