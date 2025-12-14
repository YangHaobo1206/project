<template>
  <PageContainer title="商家管理" subtitle="基础信息与营业状态">
    <template #actions>
      <el-button type="primary" size="small" @click="openCreate">新增商家</el-button>
      <el-button size="small" class="btn-soft" @click="load">刷新</el-button>
    </template>
    <el-table :data="shops" class="page-table" :loading="loading" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="商家名称" />
      <el-table-column prop="description" label="简介" />
      <el-table-column prop="address" label="门店地址" />
      <el-table-column label="审核状态" width="120">
        <template #default="scope">
          <el-tag :type="statusTag(scope.row.status)">
            {{ statusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
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
          <el-button size="small" type="warning" @click="toggle(scope.row)" :disabled="scope.row.status !== 'APPROVED'">
            {{ scope.row.online ? '下线' : '上线' }}
          </el-button>
          <el-button
            v-if="scope.row.status === 'PENDING'"
            size="small"
            type="primary"
            plain
            @click="approve(scope.row)"
          >
            审核通过
          </el-button>
          <el-button
            v-if="scope.row.status === 'PENDING'"
            size="small"
            type="danger"
            plain
            @click="reject(scope.row)"
          >
            拒绝
          </el-button>
          <el-button size="small" type="danger" @click="remove(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </PageContainer>

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
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import PageContainer from '../components/PageContainer.vue'
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

const statusText = (status) => {
  if (status === 'APPROVED') return '通过'
  if (status === 'REJECTED') return '已拒绝'
  return '待审核'
}
const statusTag = (status) => {
  if (status === 'APPROVED') return 'success'
  if (status === 'REJECTED') return 'danger'
  return 'warning'
}

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
  if (row.status !== 'APPROVED') {
    ElMessage.warning('未通过审核，无法切换营业状态')
    return
  }
  const target = row.online ? 'offline' : 'online'
  await http.post(`/api/shops/${row.id}/${target}`)
  ElMessage.success(row.online ? '已下线' : '已上线')
  await load()
}

const approve = async (row) => {
  await http.post(`/api/shops/${row.id}/approve`)
  ElMessage.success('已通过审核')
  await load()
}

const reject = async (row) => {
  await http.post(`/api/shops/${row.id}/reject`)
  ElMessage.success('已拒绝')
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

<style scoped>
.page-table :deep(.el-table__cell) {
  padding: 10px 8px;
}
</style>
