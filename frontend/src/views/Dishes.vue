<template>
  <div>
    <el-card class="glass-card page-card">
      <template #header>
        <div class="header-bar sticky-bar">
          <div class="section-title">
            <span>菜品管理</span>
            <small>上架、价格及描述</small>
          </div>
          <div class="actions">
            <el-button type="primary" @click="openCreate">新增菜品</el-button>
            <el-button class="btn-soft" @click="load">刷新</el-button>
          </div>
        </div>
      </template>

      <el-table :data="dishes" style="width: 100%" :loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="price" label="价格" />
        <el-table-column prop="category" label="分类" />
        <el-table-column label="所属商家">
          <template #default="scope">
            {{ scope.row.shop?.name || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.available ? 'success' : 'info'">
              {{ scope.row.available ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260">
          <template #default="scope">
            <el-button size="small" @click="openEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="warning" @click="toggle(scope.row)">
              {{ scope.row.available ? '下架' : '上架' }}
            </el-button>
            <el-button size="small" type="danger" @click="remove(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑菜品' : '新增菜品'" width="500px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="名称" required>
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="价格" required>
          <el-input v-model.number="form.price" type="number" min="0" />
        </el-form-item>
        <el-form-item label="分类">
          <el-input v-model="form.category" />
        </el-form-item>
        <el-form-item label="商家" required>
          <el-select v-model="form.shopId" placeholder="选择商家">
            <el-option v-for="shop in shops" :key="shop.id" :label="shop.name" :value="shop.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import http from '../api/http'

const dishes = ref([])
const shops = ref([])
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const form = reactive({
  id: null,
  name: '',
  price: 0,
  category: '',
  shopId: null
})

const resetForm = () => {
  form.id = null
  form.name = ''
  form.price = 0
  form.category = ''
  form.shopId = null
}

const load = async () => {
  loading.value = true
  try {
    const [shopList, dishList] = await Promise.all([http.get('/api/shops'), http.get('/api/dishes')])
    shops.value = shopList
    dishes.value = dishList
  } finally {
    loading.value = false
  }
}

const openCreate = () => {
  resetForm()
  dialogVisible.value = true
}

const openEdit = (row) => {
  form.id = row.id
  form.name = row.name
  form.price = Number(row.price) || 0
  form.category = row.category || ''
  form.shopId = row.shop?.id || null
  dialogVisible.value = true
}

const save = async () => {
  if (saving.value) return
  if (!form.name || !form.shopId) {
    ElMessage.warning('请完整填写菜品信息')
    return
  }
  saving.value = true
  try {
    const payload = {
      name: form.name,
      category: form.category,
      price: Number(form.price)
    }
    if (form.id) {
      await http.put(`/api/dishes/${form.id}`, payload, { params: { shopId: form.shopId } })
      ElMessage.success('更新成功')
    } else {
      await http.post('/api/dishes', payload, { params: { shopId: form.shopId } })
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    await load()
  } catch (error) {
    ElMessage.error(error?.message || '操作失败')
  } finally {
    saving.value = false
  }
}

const toggle = async (row) => {
  const action = row.available ? 'off' : 'on'
  await http.post(`/api/dishes/${row.id}/${action}`)
  ElMessage.success(row.available ? '已下架' : '已上架')
  await load()
}

const remove = async (row) => {
  await ElMessageBox.confirm(`确定删除菜品 ${row.name} 吗？`, '提示', { type: 'warning' })
  await http.delete(`/api/dishes/${row.id}`)
  ElMessage.success('删除成功')
  await load()
}

onMounted(load)
</script>
