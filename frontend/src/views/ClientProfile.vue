<template>
  <PageContainer title="个人中心" subtitle="查看当前登录账号信息">
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
  </PageContainer>
</template>

<script setup>
import { computed } from 'vue'
import PageContainer from '../components/PageContainer.vue'
import { useAuthStore } from '../store/auth'

const auth = useAuthStore()
const user = computed(() => auth.user)
</script>

<style scoped>
.info-list {
  display: grid;
  gap: 12px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 12px;
  border: 1px solid var(--card-border);
  border-radius: 10px;
  background: #f8fafc;
}

.label {
  color: var(--text-muted);
}

.value {
  font-weight: 600;
  color: var(--text-primary);
}
</style>
