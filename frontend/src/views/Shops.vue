<template>
  <div>
    <el-card class="glass-card page-card">
      <template #header>
        <div class="header-bar sticky-bar">
          <div class="section-title">
            <span>商家管理</span>
            <small>基础信息与营业状态</small>
          </div>
          <div class="actions">
            <el-button type="primary" @click="openCreate">新增商家</el-button>
            <el-button class="btn-soft" @click="load">刷新</el-button>
          </div>
        </div>
      </template>
      <el-table :data="shops" style="width: 100%" :loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="商家名称" />
        <el-table-column prop="description" label="简介" />
        <el-table-column prop="address" label="门店地址" />
        <el-table-column label="营业状态" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.online ? 'success' : 'info'">
              {{ scope.row.online ? '营业' : '休息' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280">
          <template #default="scope">
            <el-button size="small" @click="openEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="warning" @click="toggle(scope.row)">
              {{ scope.row.online ? '下线' : '上线' }}
            </el-button>
            <el-button size="small" type="danger" @click="remove(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑商家' : '新增商家'" width="480px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="名称" required>
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="form.description" />
        </el-form-item>
        <el-form-item label="门店地址" required>
          <el-input v-model="form.address" />
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

const shops = ref([])
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const form = reactive({
  id: null,
  name: '',
  description: '',
  address: ''
})

const resetForm = () => {
  form.id = null
  form.name = ''
  form.description = ''
  form.address = ''
}

const load = async () => {
  loading.value = true
  try {
    shops.value = await http.get('/api/shops')
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
  form.description = row.description || ''
  form.address = row.address || ''
  dialogVisible.value = true
}

const save = async () => {
  if (saving.value) return
  if (!form.name || !form.address) {
    ElMessage.warning('请填写名称和地址')
    return
  }
  saving.value = true
  try {
    const payload = {
      name: form.name,
      description: form.description,
      address: form.address
    }
    if (form.id) {
      await http.put(`/api/shops/${form.id}`, payload)
      ElMessage.success('更新成功')
    } else {
      await http.post('/api/shops', payload)
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
  await http.post(`/api/shops/${row.id}/online`, { online: !row.online })
  ElMessage.success(row.online ? '已下线' : '已上线')
  await load()
}

const remove = async (row) => {
  await ElMessageBox.confirm(`确定删除商家 ${row.name} 吗？`, '提示', { type: 'warning' })
  await http.delete(`/api/shops/${row.id}`)
  ElMessage.success('删除成功')
  await load()
}

onMounted(load)
</script>
