<template>
  <div class="auth-wrap">
    <div class="auth-panel glass-card">
      <h2 style="margin-bottom: 12px">账号注册</h2>
      <el-form :model="form" @submit.prevent="onSubmit" label-position="top">
        <el-form-item label="用户名">
          <el-input v-model="form.username" autocomplete="username" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" autocomplete="tel" />
        </el-form-item>
        <el-form-item label="收货地址">
          <el-input v-model="form.address" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input type="password" v-model="form.password" autocomplete="new-password" />
        </el-form-item>
        <el-button type="primary" native-type="submit" @click="onSubmit" class="btn-primary" style="width: 100%">
          注册并登录
        </el-button>
        <el-button link style="width: 100%; margin-top: 8px" @click="router.push('/login')">
          已有账号？去登录
        </el-button>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../store/auth'
import http from '../api/http'

const router = useRouter()
const auth = useAuthStore()
const form = reactive({ username: '', password: '', phone: '', address: '' })

const onSubmit = async () => {
  try {
    await http.post('/api/auth/register', form)
    const token = await http.post('/api/auth/login', { username: form.username, password: form.password })
    auth.setAuth({ token, user: null })
    const user = await http.get('/api/auth/me')
    auth.setAuth({ token, user })
    if (user?.role === 'ADMIN') {
      router.push('/admin/users')
    } else {
      router.push('/client/shops')
    }
  } catch (err) {
    const message = err?.message || err?.data?.message || err?.response?.data?.message || '注册失败'
    ElMessage.error(message)
  }
}
</script>
