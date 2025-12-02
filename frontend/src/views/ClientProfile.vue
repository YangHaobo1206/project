<template>
  <section class="client-card glass-card">
    <header class="card-header">
      <div>
        <h2>个人中心</h2>
        <p class="muted">查看当前登录账号信息</p>
      </div>
    </header>

    <div class="info-list">
      <div class="info-row">
        <span class="label">用户名</span>
        <span class="value">{{ user?.username || '-' }}</span>
      </div>
      <div class="info-row">
        <span class="label">角色</span>
        <span class="value">{{ user?.role || '-' }}</span>
      </div>
      <div class="info-row">
        <span class="label">手机号</span>
        <span class="value">{{ user?.phone || '未填写' }}</span>
      </div>
      <div class="info-row">
        <span class="label">收货地址</span>
        <span class="value">{{ user?.address || '未填写' }}</span>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed } from 'vue'
import { useAuthStore } from '../store/auth'

const auth = useAuthStore()
const user = computed(() => auth.user)
</script>

<style scoped>
.client-card {
  padding: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.card-header h2 {
  margin: 0 0 4px;
}

.muted {
  margin: 0;
  color: var(--text-muted);
}

.info-list {
  display: grid;
  gap: 12px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 12px;
  border: 1px solid rgba(236, 155, 52, 0.2);
  border-radius: 10px;
  background: rgba(255, 248, 224, 0.7);
}

.label {
  color: var(--text-muted);
}

.value {
  font-weight: 600;
  color: var(--text-primary);
}
</style>
