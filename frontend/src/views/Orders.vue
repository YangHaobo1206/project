<template>
  <el-card>
    <template #header>
      <div style="display:flex;justify-content:space-between;align-items:center">
        <span>订单管理</span>
        <el-button type="primary" @click="simulateOrder">模拟下单</el-button>
      </div>
    </template>
    <el-table :data="orders" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="status" label="状态" />
      <el-table-column prop="totalAmount" label="金额" />
      <el-table-column prop="createdAt" label="创建时间" />
      <el-table-column label="操作" width="240">
        <template #default="scope">
          <el-button size="small" @click="pay(scope.row.id)">支付</el-button>
          <el-button size="small" @click="accept(scope.row.id)">接单</el-button>
          <el-button size="small" type="danger" @click="cancel(scope.row.id)">取消</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import http from '../api/http'
import { ElMessage } from 'element-plus'

const orders = ref([])

const load = async () => {
  const res = await http.get('/api/orders/user/1')
  orders.value = res.data
}

const simulateOrder = async () => {
  await http.post('/api/orders', { status: 'CREATED', totalAmount: 30 }, { params: { userId: 1, shopId: 1 } })
  ElMessage.success('下单成功')
  load()
}

const pay = async (id) => {
  await http.post(`/api/orders/${id}/pay`)
  load()
}

const accept = async (id) => {
  await http.post(`/api/orders/${id}/accept`)
  load()
}

const cancel = async (id) => {
  await http.post(`/api/orders/${id}/cancel`)
  load()
}

onMounted(load)
</script>
