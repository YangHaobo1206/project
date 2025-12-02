<template>
  <div class="client-shell">
    <aside class="client-side glass-card">
      <div class="side-brand">
        <span class="dot" />
        <div>
          <div class="title">点餐中心</div>
          <small class="muted">Client</small>
        </div>
      </div>
      <nav class="side-nav">
        <RouterLink :class="['side-link', { active: isActive('/client/shops') }]" to="/client/shops">商家</RouterLink>
        <RouterLink :class="['side-link', { active: isActive('/client/dishes') }]" to="/client/dishes">菜品</RouterLink>
        <RouterLink :class="['side-link', { active: isActive('/client/orders') }]" to="/client/orders">我的订单</RouterLink>
        <RouterLink :class="['side-link', { active: isActive('/client/profile') }]" to="/client/profile">我的信息</RouterLink>
      </nav>
    </aside>

    <section class="client-main">
      <header class="client-top glass-card">
        <div>
          <h2 class="welcome">欢迎光临</h2>
          <p class="muted">挑选一家正在营业的店铺下单</p>
        </div>
        <el-button class="btn-soft" size="small" @click="logout">退出登录</el-button>
      </header>
      <main class="client-body">
        <router-view />
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
.client-shell {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 220px 1fr;
  gap: 18px;
  background: var(--bg-gradient);
  color: var(--text-primary);
  padding: 18px;
}

.client-side {
  padding: 20px 16px;
}

.side-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
}

.dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: linear-gradient(135deg, #f59e0b, #f97316);
  box-shadow: 0 0 12px rgba(245, 158, 11, 0.5);
}

.title {
  font-weight: 700;
}

.side-nav {
  display: grid;
  gap: 8px;
}

.side-link {
  padding: 10px 12px;
  border-radius: 10px;
  color: var(--text-primary);
  text-decoration: none;
  transition: all 0.18s ease;
}

.side-link:hover {
  background: rgba(255, 211, 128, 0.4);
}

.side-link.active {
  background: rgba(255, 211, 128, 0.7);
  box-shadow: 0 8px 22px rgba(245, 158, 11, 0.25);
}

.client-main {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.client-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 18px;
}

.welcome {
  margin: 0 0 6px;
}

.client-body {
  padding: 0;
}

.muted {
  color: var(--text-muted);
  margin: 0;
}
</style>
