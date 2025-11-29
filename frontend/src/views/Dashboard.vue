<template>
  <el-container style="height: 100vh">
    <el-aside width="200px">
      <h3 style="padding: 16px">外卖管理</h3>
      <el-menu router>
        <el-menu-item index="/users">用户管理</el-menu-item>
        <el-menu-item index="/shops">商家管理</el-menu-item>
        <el-menu-item index="/dishes">菜品管理</el-menu-item>
        <el-menu-item index="/orders">订单管理</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header style="display: flex; justify-content: space-between; align-items: center">
        <div>欢迎回来</div>
        <el-button type="text" @click="onLogout">退出</el-button>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth'

const router = useRouter()
const auth = useAuthStore()

const onLogout = () => {
  auth.logout()
  router.push('/login')
}
</script>
