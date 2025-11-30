<template>
  <div>
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>用户列表</span>
          <el-button type="primary" @click="load">刷新</el-button>
        </div>
      </template>
      <el-table :data="users" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="phone" label="电话" />
        <el-table-column prop="role" label="角色" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import http from '../api/http'

const users = ref([])

const load = async () => {
  const res = await http.get('/api/auth/users')
  users.value = res
}

onMounted(load)
</script>
