<template>
  <el-container style="height: 100vh; color: var(--text-primary)">
    <el-aside width="220px" class="side glass-card">
      <div class="brand">
        <div class="brand-dot" />
        <div>
          <div class="brand-title">外卖管理</div>
          <div class="brand-sub">Admin Panel</div>
        </div>
      </div>
      <el-menu
        router
        class="menu-compact"
        background-color="transparent"
        text-color="var(--text-muted)"
        active-text-color="var(--accent)"
      >
        <el-menu-item index="/admin/users">用户管理</el-menu-item>
        <el-menu-item index="/admin/shops">商家管理</el-menu-item>
        <el-menu-item index="/admin/dishes">菜品管理</el-menu-item>
        <el-menu-item index="/admin/orders">订单管理</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header
        class="page-header"
        style="display: flex; justify-content: space-between; align-items: center; padding: 12px 20px; background: rgba(255,245,230,0.9);"
      >
        <div style="font-weight: 600;">欢迎回来，管理员</div>
        <el-button class="btn-soft" @click="onLogout">退出登录</el-button>
      </el-header>
      <el-main style="padding: 20px; background: transparent;">
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

<style scoped>
.side {
  padding: 16px 12px;
  background: rgba(255, 248, 231, 0.9);
}
.brand {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px;
  margin-bottom: 8px;
}
.brand-dot {
  width: 14px;
  height: 14px;
  border-radius: 50%;
  background: linear-gradient(135deg, #f59e0b, #fb7185);
  box-shadow: 0 0 18px rgba(250, 159, 21, 0.6);
}
.brand-title {
  font-weight: 700;
  color: var(--text-primary);
}
.brand-sub {
  font-size: 12px;
  color: var(--text-muted);
}
</style>
