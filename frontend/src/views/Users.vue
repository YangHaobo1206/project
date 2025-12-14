<template>
  <PageContainer title="用户管理" subtitle="账号、角色与联系方式">
    <template #actions>
      <el-button type="primary" size="small" @click="openCreate">新增用户</el-button>
      <el-button size="small" class="btn-soft" @click="load">刷新</el-button>
    </template>
    <el-table :data="users" class="page-table" :loading="loading" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="phone" label="电话" />
      <el-table-column prop="address" label="收货地址" />
      <el-table-column prop="role" label="角色" />
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button size="small" @click="openEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="remove(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </PageContainer>

  <el-dialog v-model="dialogVisible" :title="form.id ? '编辑用户' : '新增用户'" width="420px">
    <el-form :model="form" label-width="90px">
      <el-form-item label="用户名" required>
        <el-input v-model="form.username" :disabled="!!form.id" />
      </el-form-item>
      <el-form-item v-if="!form.id" label="密码" required>
        <el-input v-model="form.password" type="password" placeholder="至少6位" />
      </el-form-item>
      <el-form-item label="电话">
        <el-input v-model="form.phone" />
      </el-form-item>
      <el-form-item label="收货地址">
        <el-input v-model="form.address" />
      </el-form-item>
      <el-form-item label="角色">
        <el-select v-model="form.role" placeholder="选择角色">
          <el-option label="管理员" value="ADMIN" />
          <el-option label="普通用户" value="USER" />
          <el-option label="商家" value="MERCHANT" />
        </el-select>
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

const users = ref([])
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const form = reactive({
  id: null,
  username: '',
  password: '',
  phone: '',
  address: '',
  role: 'USER'
})

const resetForm = () => {
  form.id = null
  form.username = ''
  form.password = ''
  form.phone = ''
  form.address = ''
  form.role = 'USER'
}

const load = async () => {
  loading.value = true
  try {
    users.value = await http.get('/api/auth/users')
  } finally {
    loading.value = false
  }
}

const openCreate = () => {
  resetForm()
  dialogVisible.value = true
}

const openEdit = (row) => {
  resetForm()
  form.id = row.id
  form.username = row.username
  form.phone = row.phone || ''
  form.address = row.address || ''
  form.role = row.role || 'USER'
  dialogVisible.value = true
}

const save = async () => {
  if (saving.value) return
  if (!form.username) {
    ElMessage.warning('请填写用户名')
    return
  }
  if (!form.id && !form.password) {
    ElMessage.warning('请填写密码')
    return
  }
  if (!form.id && form.role === 'MERCHANT') {
    ElMessage.warning('商家请通过注册流程创建以便提交店铺审核信息')
    return
  }

  saving.value = true
  try {
    const payload = {
      phone: form.phone,
      role: form.role,
      address: form.address
    }
    if (form.id) {
      await http.put(`/api/auth/${form.id}`, payload)
      ElMessage.success('更新成功')
    } else {
      await http.post('/api/auth/register', {
        username: form.username,
        password: form.password,
        ...payload
      })
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

const remove = async (row) => {
  await ElMessageBox.confirm(`确定删除用户 ${row.username} 吗？`, '提示', { type: 'warning' })
  await http.delete(`/api/auth/${row.id}`)
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
