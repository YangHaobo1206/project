<template>
  <div class="auth-wrap">
    <div class="auth-panel glass-card">
      <div class="auth-header">
        <h2>欢迎回来</h2>
        <p>请输入账号密码登录</p>
      </div>
      <el-form :model="form" @submit.prevent="onSubmit" label-position="top">
        <el-form-item label="用户名">
          <el-input v-model="form.username" autocomplete="username" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input type="password" v-model="form.password" autocomplete="current-password" />
        </el-form-item>
        <el-button
          type="primary"
          native-type="submit"
          :loading="loading"
          @click="onSubmit"
          class="btn-primary"
          style="width: 100%"
        >
          登录
        </el-button>
        <el-button link style="width: 100%; margin-top: 8px" @click="router.push('/register')">
          没有账号？去注册
        </el-button>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth'
import http from '../api/http'

const router = useRouter()
const auth = useAuthStore()
const form = reactive({ username: '', password: '' })
const loading = ref(false)

const onSubmit = async () => {
  if (loading.value) return
  loading.value = true
  try {
    const token = await http.post('/api/auth/login', form)
    auth.setAuth({ token, user: null })
    const user = await http.get('/api/auth/me')
    auth.setAuth({ token, user })
    if (user?.role === 'ADMIN') {
      router.push('/admin/users')
    } else {
      router.push('/client/shops')
    }
  } catch (error) {
    const message =
      error?.response?.data?.message ||
      error?.message ||
      error?.msg ||
      '登录失败，请稍后再试'
    ElMessage.error(message)
  } finally {
    loading.value = false
  }
}
</script>
