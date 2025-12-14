<template>
  <PageContainer title="商家列表" subtitle="挑选喜欢的商家查看菜单并下单">
    <template #actions>
      <el-button size="small" class="btn-soft" :loading="loading" @click="refresh">
        刷新
      </el-button>
    </template>

    <!-- ===== 轮播图 ===== -->
    <div v-if="carouselList.length" class="carousel-wrapper">
      <div class="carousel-stage">
        <el-carousel indicator-position="outside" height="100%">
          <el-carousel-item v-for="item in carouselList" :key="item.id">
            <div class="carousel-slide">
              <img
                v-if="item.imageUrl"
                :src="toImageUrl(item.imageUrl)"
                alt=""
              />
              <div class="carousel-info">
                <div class="shop-name">
                  {{ item.shop?.name || '商家' }}
                </div>
                <div class="dish-name">
                  {{ item.dish?.name || '特色菜品' }}
                </div>
              </div>
            </div>
          </el-carousel-item>
        </el-carousel>
      </div>
    </div>

    <!-- ===== 商家列表 ===== -->
    <div v-if="loading" class="placeholder">加载中...</div>

    <div v-else class="shops-grid">
      <div
        v-for="shop in client.shops"
        :key="shop.id"
        class="shop-tile"
        :class="{ active: isActive(shop.id) }"
        @click="select(shop.id)"
      >
        <div class="shop-head">
          <div class="shop-name">{{ shop.name }}</div>
          <el-tag :type="shop.online ? 'success' : 'info'" size="small">
            {{ shop.online ? '营业中' : '休息' }}
          </el-tag>
        </div>

        <span class="category">
          {{ shop.description || '主营菜品丰富' }}
        </span>

        <p class="address">地址：{{ shop.address || '未填写' }}</p>

        <div class="shop-actions">
          <el-button
            type="primary"
            size="small"
            class="btn-primary"
            @click.stop="goDishes(shop.id)"
          >
            查看菜单
          </el-button>
          <span v-if="isActive(shop.id)" class="current">当前</span>
        </div>
      </div>
    </div>
  </PageContainer>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import PageContainer from '../components/PageContainer.vue'
import { useClientStore } from '../store/client'

const client = useClientStore()
const router = useRouter()

const loading = computed(() => client.loadingShops)
const apiBase = import.meta.env.VITE_API_BASE || 'http://localhost:8080'

const carouselList = computed(() => (client.carousels || []).slice(0, 5))

const isActive = (id) => String(id) === String(client.selectedShopId)
const select = (id) => client.setSelectedShop(id)
const refresh = () => client.loadShops()

const goDishes = (id) => {
  select(id)
  router.push('/client/dishes')
}

const toImageUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  if (url.startsWith('/')) return `${apiBase}${url}`
  return `${apiBase}/${url}`
}

onMounted(async () => {
  if (!client.shops.length) await client.loadShops()
  if (!client.carousels.length) await client.loadCarousels()
})
</script>

<style scoped>
/* ===== 通用 ===== */
.placeholder {
  padding: 24px;
  text-align: center;
  color: var(--text-muted);
}

/* ===== 轮播图（最终稳定版） ===== */

.carousel-wrapper {
  display: flex;
  justify-content: center;
  margin-bottom: 28px;
}

.carousel-stage {
  width: 100%;
  max-width: 950px;
  aspect-ratio: 8 / 5;
  max-height: 450px; /* ✅ 关键：强制限制高度 */
  border-radius: var(--radius-lg);
  overflow: hidden;
  background: linear-gradient(180deg, #2a2a2a, #1f1f1f);
  box-shadow: 0 18px 36px rgba(0, 0, 0, 0.22);
}

/* el-carousel 撑满 */
.carousel-stage :deep(.el-carousel),
.carousel-stage :deep(.el-carousel__container) {
  height: 100%;
}

.carousel-slide {
  position: relative;
  width: 100%;
  height: 100%;
}

.carousel-slide img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center 48%; /* ✅ 向上裁剪 */
  filter: brightness(0.95) contrast(1.05);
  transition: transform 0.5s ease;
}

.carousel-slide:hover img {
  transform: scale(1.03);
}

.carousel-info {
  position: absolute;
  left: 16px;
  bottom: 16px;
  padding: 8px 14px;
  background: linear-gradient(
    90deg,
    rgba(0, 0, 0, 0.7),
    rgba(0, 0, 0, 0.4)
  );
  color: #fff;
  border-radius: 10px;
  font-size: 13px;
}

.carousel-info .dish-name {
  font-weight: 600;
  margin-top: 2px;
}

/* 指示器 */
:deep(.el-carousel__indicators--outside button) {
  background: rgba(0, 0, 0, 0.25);
  height: 6px;
  width: 18px;
  border-radius: 999px;
}

:deep(.el-carousel__indicator.is-active button) {
  background: var(--accent);
}

/* ===== 商家卡片 ===== */

.shops-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 14px;
}

.shop-tile {
  padding: 14px;
  background: var(--card-bg);
  border-radius: var(--radius-lg);
  border: 1px solid var(--card-border);
  cursor: pointer;
  transition: all 0.18s ease;
}

.shop-tile:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 22px rgba(255, 138, 0, 0.12);
}

.shop-tile.active {
  border-color: var(--accent);
  box-shadow: 0 12px 26px rgba(255, 138, 0, 0.18);
}

.shop-head {
  display: flex;
  justify-content: space-between;
  margin-bottom: 6px;
}

.category {
  display: inline-block;
  margin-bottom: 6px;
  background: #fff7ed;
  border: 1px solid #ffd8a8;
  padding: 4px 8px;
  border-radius: 8px;
  font-size: 12px;
  color: #b45309;
  font-weight: 600;
}

.address {
  margin: 0 0 10px;
}

.shop-actions {
  display: flex;
  gap: 10px;
}

.current {
  font-size: 12px;
  color: #d46b08;
}
</style>
