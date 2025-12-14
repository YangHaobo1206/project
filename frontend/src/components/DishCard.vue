<template>
  <div class="dish-card">
    <div class="dish-image" :class="{ empty: !dish.imageUrl }">
      <img v-if="dish.imageUrl" :src="toImageUrl(dish.imageUrl)" alt="" />
      <span v-else class="muted">无图</span>
      <el-tag class="status-tag" :type="dish.available ? 'success' : 'info'" size="small">
        {{ dish.available ? '可售' : '售罄' }}
      </el-tag>
    </div>
    <div class="dish-body">
      <div class="dish-title">
        <div>
          <div class="dish-name">{{ dish.name }}</div>
          <small class="muted">{{ dish.category || '未分类' }}</small>
        </div>
        <div class="dish-price">¥{{ Number(dish.price || 0).toFixed(2) }}</div>
      </div>
      <div class="muted shop-name">{{ dish.shop?.name || '未关联商家' }}</div>
      <slot />
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  dish: { type: Object, required: true },
  apiBase: { type: String, default: '' }
})

const toImageUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  const base = props.apiBase || import.meta.env.VITE_API_BASE || 'http://localhost:8080'
  return `${base}${url}`
}
</script>

<style scoped>
.dish-card {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 0;
  overflow: hidden;
  border-radius: var(--radius-lg);
  border: 1px solid var(--card-border);
  background: var(--card-bg);
}
.dish-image {
  position: relative;
  height: 170px;
  background: #f7f7f7;
  display: flex;
  align-items: center;
  justify-content: center;
}
.dish-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.dish-image.empty {
  color: var(--text-muted);
}
.status-tag {
  position: absolute;
  right: 10px;
  top: 10px;
}
.dish-body {
  padding: 12px 14px 14px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.dish-title {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 8px;
}
.dish-name {
  font-weight: 700;
  color: var(--text-primary);
}
.dish-price {
  font-size: 18px;
  font-weight: 800;
  color: var(--accent);
}
.shop-name {
  min-height: 18px;
}
.muted {
  color: var(--text-muted);
}
</style>
