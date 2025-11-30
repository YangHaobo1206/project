<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2>账号注册</h2>
      <el-form :model="form" @submit.prevent="onSubmit">
        <el-form-item label="用户名">
          <el-input v-model="form.username" autocomplete="username" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" autocomplete="tel" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input type="password" v-model="form.password" autocomplete="new-password" />
        </el-form-item>
        <el-button type="primary" @click="onSubmit" style="width: 100%">注册并登录</el-button>
        <el-button link style="width: 100%; margin-top: 8px" @click="router.push('/login')">已有账号？去登录</el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth'
import http from '../api/http'

const router = useRouter()
const auth = useAuthStore()
const form = reactive({ username: '', password: '', phone: '' })

const onSubmit = async () => {
  await http.post('/api/auth/register', form)
  const token = await http.post('/api/auth/login', { username: form.username, password: form.password })
  const user = await http.get('/api/auth/me')
  auth.setAuth({ token, user })
  router.push('/')
}
</script>

<style scoped>
.login-container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background: #f2f2f2;
}
.login-card {
  width: 360px;
}
</style>
