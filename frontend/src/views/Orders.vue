<template>
  <div>
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>订单管理</span>
          <el-button type="primary" @click="load">刷新</el-button>
        </div>
      </template>
      <el-table :data="orders" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="user" label="用户" />
        <el-table-column prop="shop" label="商家" />
        <el-table-column prop="total" label="总价" />
        <el-table-column prop="status" label="状态" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import http from '../api/http'

const orders = ref([])

const load = async () => {
  const res = await http.get('/api/orders')
  orders.value = res
}

onMounted(load)
</script>
