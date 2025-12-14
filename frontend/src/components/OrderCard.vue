<template>
  <div class="order-card">
    <div class="order-head">
      <div>
        <div class="order-id">订单 #{{ order.id }}</div>
        <small class="muted">{{ order.createdAt || '-' }}</small>
      </div>
      <el-tag :type="statusType(order.status)" size="small">{{ statusText(order.status) }}</el-tag>
    </div>
    <div class="order-body">
      <div class="muted">商家：{{ order.shop?.name || '-' }}</div>
      <div class="muted">金额：¥{{ Number(order.totalAmount || 0).toFixed(2) }}</div>
    </div>
    <div class="order-actions">
      <slot />
    </div>
  </div>
</template>

<script setup>
const props = defineProps({ order: { type: Object, required: true } })
const statusText = (s) =>
  ({ PAID: '已支付', ACCEPTED: '已接单', COMPLETED: '已完成', CANCELLED: '已取消', CREATED: '待支付' }[s] || s || '未知')
const statusType = (s) =>
  ({ PAID: 'success', ACCEPTED: 'warning', COMPLETED: 'success', CANCELLED: 'info', CREATED: 'info' }[s] || 'info')
</script>

<style scoped>
.order-card {
  padding: 14px;
  display: grid;
  gap: 8px;
  border: 1px solid var(--card-border);
  border-radius: var(--radius-md);
  background: var(--card-bg);
}
.order-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.order-id {
  font-weight: 700;
}
.order-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
.muted {
  color: var(--text-muted);
}
</style>
