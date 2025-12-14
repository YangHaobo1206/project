<template>
  <div class="app-shell">
    <aside class="shell-nav">
      <div class="nav-brand">
        <div class="nav-dot" />
        <div>
          <div class="nav-title">外卖平台</div>
          <div class="nav-sub">Admin</div>
        </div>
      </div>
      <el-menu
        router
        class="nav-menu"
        :default-active="activePath"
        background-color="transparent"
        text-color="var(--text-muted)"
        active-text-color="var(--accent)"
      >
        <el-menu-item index="/admin/dashboard">数据大屏</el-menu-item>
        <el-menu-item index="/admin/users">用户管理</el-menu-item>
        <el-menu-item index="/admin/shops">商家管理</el-menu-item>
        <el-menu-item index="/admin/dishes">菜品管理</el-menu-item>
        <el-menu-item index="/admin/orders">订单管理</el-menu-item>
        <el-menu-item index="/admin/carousels">轮播图管理</el-menu-item>
      </el-menu>
    </aside>
    <div class="shell-main">
      <header class="shell-topbar">
        <div class="topbar-title">后台管理</div>
        <div class="topbar-actions">
          <el-button size="small" text @click="onLogout">退出登录</el-button>
        </div>
      </header>
      <main class="shell-content">
        <div class="content-boundary">
          <router-view />
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const activePath = computed(() => (route.path.startsWith('/admin') ? route.path : '/admin/dashboard'))

const onLogout = () => {
  auth.logout()
  router.push('/login')
}
</script>

<style scoped>
.nav-menu {
  flex: 1;
}

.nav-menu :deep(.el-menu-item) {
  border-radius: 10px;
  margin: 2px 4px;
}

.nav-menu :deep(.el-menu-item.is-active) {
  background: #eef2ff;
  color: var(--accent);
}

.topbar-title {
  font-weight: 600;
}
</style>
