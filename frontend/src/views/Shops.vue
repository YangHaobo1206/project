<template>
  <div>
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>商家管理</span>
          <el-button type="primary" @click="load">刷新</el-button>
        </div>
      </template>
      <el-table :data="shops" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="商家" />
        <el-table-column prop="address" label="地址" />
        <el-table-column prop="open" label="营业状态">
          <template #default="scope">
            <el-tag :type="scope.row.open ? 'success' : 'info'">{{ scope.row.open ? '营业' : '休息' }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import http from '../api/http'

const shops = ref([])

const load = async () => {
  const res = await http.get('/api/shops')
  shops.value = res
}

onMounted(load)
</script>
