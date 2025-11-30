<template>
  <div>
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>菜品管理</span>
          <el-button type="primary" @click="load">刷新</el-button>
        </div>
      </template>
      <el-form :inline="true" :model="form" style="margin-bottom: 12px">
        <el-form-item label="名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.category" placeholder="选择商家">
            <el-option v-for="shop in shops" :key="shop.id" :label="shop.name" :value="shop.name" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="create">新增菜品</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="dishes" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="菜名" />
        <el-table-column prop="price" label="价格" />
        <el-table-column prop="category" label="分类" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import http from '../api/http'

const dishes = ref([])
const shops = ref([])
const form = reactive({ name: '', price: 0, category: '' })

const load = async () => {
  shops.value = await http.get('/api/shops')
  dishes.value = await http.get('/api/dishes')
}

const create = async () => {
  if (!form.name || !form.category) return
  await http.post('/api/dishes', form)
  form.name = ''
  form.price = 0
  await load()
}

onMounted(load)
</script>
