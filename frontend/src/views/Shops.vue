<template>
  <el-card>
    <template #header>
      <div style="display:flex;justify-content:space-between;align-items:center">
        <span>商家管理</span>
        <el-button type="primary" @click="createShop">新增商家</el-button>
      </div>
    </template>
    <el-table :data="shops" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="description" label="描述" />
      <el-table-column label="状态">
        <template #default="scope">
          <el-tag :type="scope.row.online ? 'success' : 'info'">{{ scope.row.online ? '在线' : '离线' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220">
        <template #default="scope">
          <el-button size="small" @click="toggle(scope.row)">{{ scope.row.online ? '下线' : '上线' }}</el-button>
          <el-button size="small" type="danger" @click="remove(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import http from '../api/http'

const shops = ref([])

const load = async () => {
  const res = await http.get('/api/shops')
  shops.value = res.data
}

const createShop = async () => {
  const name = prompt('请输入商家名称')
  if (!name) return
  await http.post('/api/shops', { name, description: '新商家' })
  load()
}

const toggle = async (shop) => {
  const url = shop.online ? `/api/shops/${shop.id}/offline` : `/api/shops/${shop.id}/online`
  await http.post(url)
  load()
}

const remove = async (id) => {
  await ElMessageBox.confirm('确定删除该商家吗?')
  await http.delete(`/api/shops/${id}`)
  ElMessage.success('删除成功')
  load()
}

onMounted(load)
</script>
