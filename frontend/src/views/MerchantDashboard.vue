<template>
  <div class="app-shell">
    <aside class="shell-nav">
      <div class="nav-brand">
        <div class="nav-dot" />
        <div>
          <div class="nav-title">商家中心</div>
          <div class="nav-sub">Merchant</div>
        </div>
      </div>
      <nav class="nav-menu">
        <RouterLink :class="['side-link', { active: isActive('/merchant/dashboard') }]" to="/merchant/dashboard">数据大屏</RouterLink>
        <RouterLink :class="['side-link', { active: isActive('/merchant/dishes') }]" to="/merchant/dishes">菜品管理</RouterLink>
        <RouterLink :class="['side-link', { active: isActive('/merchant/orders') }]" to="/merchant/orders">订单管理</RouterLink>
      </nav>
    </aside>

    <section class="shell-main">
      <header class="shell-topbar">
        <div class="topbar-title">商家端</div>
        <div class="topbar-actions">
          <el-button size="small" text @click="logout">退出登录</el-button>
        </div>
      </header>
      <main class="shell-content">
        <div class="content-boundary">
          <router-view />
        </div>
      </main>
    </section>
  </div>
</template>

<script setup>
import { useRoute } from 'vue-router'
import { useAuthStore } from '../store/auth'

const route = useRoute()
const auth = useAuthStore()
const isActive = (path) => route.path.startsWith(path)

const logout = () => {
  auth.logout()
  window.location.href = '/login'
}
</script>

<style scoped>
.nav-menu {
  display: grid;
  gap: 6px;
}

.side-link {
  padding: 10px 12px;
  border-radius: 10px;
  color: var(--text-primary);
  text-decoration: none;
  transition: all 0.18s ease;
}

.side-link:hover {
  background: #eef2ff;
}

.side-link.active {
  background: #e0e7ff;
  color: var(--accent);
}

.topbar-title {
  font-weight: 600;
}
</style>
