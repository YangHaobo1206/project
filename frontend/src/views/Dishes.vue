<template>
  <el-card>
    <template #header>
      <div style="display:flex;justify-content:space-between;align-items:center">
        <span>菜品管理</span>
        <el-button type="primary" @click="createDish">新增菜品</el-button>
      </div>
    </template>
    <el-select v-model="shopId" placeholder="选择商家" style="width:200px" @change="load">
      <el-option v-for="shop in shops" :key="shop.id" :label="shop.name" :value="shop.id" />
    </el-select>
    <el-table :data="dishes" style="width: 100%; margin-top: 12px">
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="category" label="分类" />
      <el-table-column prop="price" label="价格" />
      <el-table-column label="状态">
        <template #default="scope">
          <el-tag :type="scope.row.available ? 'success' : 'info'">{{ scope.row.available ? '在售' : '已下架' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button size="small" @click="toggle(scope.row)">{{ scope.row.available ? '下架' : '上架' }}</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../api/http'

const shops = ref([])
const dishes = ref([])
const shopId = ref()

const loadShops = async () => {
  const res = await http.get('/api/shops')
  shops.value = res.data
  if (!shopId.value && shops.value.length) {
    shopId.value = shops.value[0].id
  }
}

const load = async () => {
  if (!shopId.value) return
  const res = await http.get(`/api/dishes/shop/${shopId.value}`)
  dishes.value = res.data
}

const createDish = async () => {
  if (!shopId.value) return
  await http.post(`/api/dishes/shop/${shopId.value}`, { name: '新菜品', price: 20, category: '默认' })
  ElMessage.success('创建成功')
  load()
}

const toggle = async (dish) => {
  const url = dish.available ? `/api/dishes/${dish.id}/off` : `/api/dishes/${dish.id}/on`
  await http.post(url)
  load()
}

onMounted(async () => {
  await loadShops()
  await load()
})
</script>
