<template>
  <div>
    <el-card class="glass-card page-card">
      <template #header>
        <div class="header-bar sticky-bar">
          <div class="section-title">
            <span>订单管理</span>
            <small>支付、接单、完成、取消</small>
          </div>
          <el-button class="btn-soft" @click="load">刷新</el-button>
        </div>
      </template>
      <el-table :data="orders" style="width: 100%" :loading="loading">
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
        <el-table-column label="操作" width="360">
          <template #default="scope">
            <el-button size="small" type="success" @click="pay(scope.row)" :disabled="scope.row.status === 'PAID'">
              标记支付
            </el-button>
            <el-button size="small" type="primary" @click="accept(scope.row)" :disabled="scope.row.status === 'ACCEPTED'">
              接单
            </el-button>
            <el-button size="small" type="warning" @click="complete(scope.row)" :disabled="scope.row.status === 'COMPLETED'">
              完成
            </el-button>
            <el-button size="small" @click="cancel(scope.row)" :disabled="scope.row.status === 'CANCELLED'">
              取消
            </el-button>
            <el-button size="small" type="danger" @click="remove(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
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

const pay = async (row) => {
  await http.post(`/api/orders/${row.id}/pay`)
  ElMessage.success('已标记支付')
  await load()
}

const accept = async (row) => {
  await http.post(`/api/orders/${row.id}/accept`)
  ElMessage.success('已接单')
  await load()
}

const complete = async (row) => {
  await http.post(`/api/orders/${row.id}/complete`)
  ElMessage.success('已完成')
  await load()
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
