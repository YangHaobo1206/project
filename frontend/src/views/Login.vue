<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2>后台登录</h2>
      <el-form :model="form" @submit.prevent="onSubmit">
        <el-form-item label="用户名">
          <el-input v-model="form.username" autocomplete="username" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input type="password" v-model="form.password" autocomplete="current-password" />
        </el-form-item>
        <el-button type="primary" @click="onSubmit" style="width: 100%">登录</el-button>
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
const form = reactive({ username: '', password: '' })

const onSubmit = async () => {
  const token = await http.post('/api/auth/login', form)
  const user = await http.get('/api/auth/me')
  auth.setAuth({ token, user: user.data })
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
