<template>
  <PageContainer title="轮播图管理" subtitle="审核商家申请并控制播放">
    <div class="section">
      <div class="section-head">
        <div>
          <h3 class="title">正在播放</h3>
          <p class="muted">最多 {{ maxPlaying }} 条，当前 {{ playingList.length }} 条</p>
        </div>
        <el-tag type="warning" v-if="playingList.length >= maxPlaying">已达上限</el-tag>
      </div>
      <el-table :data="playingList" class="page-table" border :loading="loading">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column label="商家/菜品">
          <template #default="scope">
            <div class="cell-main">
              <strong>{{ scope.row.shop?.name || '-' }}</strong>
              <span class="muted"> · {{ scope.row.dish?.name || '-' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="图片" width="120">
          <template #default="scope">
            <img v-if="scope.row.imageUrl" :src="toImageUrl(scope.row.imageUrl)" class="thumb" alt="" />
            <span v-else class="muted">-</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="120">
          <template #default="scope">
            <el-tag type="success">播放中</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140">
          <template #default="scope">
            <el-button size="small" type="danger" link @click="stop(scope.row)">停止播放</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="section">
      <div class="section-head">
        <div>
          <h3 class="title">待审核</h3>
          <p class="muted">审核通过后将进入播放队列</p>
        </div>
      </div>
      <el-table :data="pendingList" class="page-table" border :loading="loading">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column label="商家/菜品">
          <template #default="scope">
            <div class="cell-main">
              <strong>{{ scope.row.shop?.name || '-' }}</strong>
              <span class="muted"> · {{ scope.row.dish?.name || '-' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="图片" width="120">
          <template #default="scope">
            <img v-if="scope.row.imageUrl" :src="toImageUrl(scope.row.imageUrl)" class="thumb" alt="" />
            <span v-else class="muted">-</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="120">
          <template #default>
            <el-tag type="warning">待审核</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220">
          <template #default="scope">
            <el-button size="small" type="success" @click="approve(scope.row)" :disabled="playingList.length >= maxPlaying">
              通过
            </el-button>
            <el-button size="small" type="danger" @click="reject(scope.row)" plain>拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="section">
      <div class="section-head">
        <div>
          <h3 class="title">已拒绝</h3>
          <p class="muted">如需重新申请，请商家再次提交</p>
        </div>
      </div>
      <el-table :data="rejectedList" class="page-table" border :loading="loading">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column label="商家/菜品">
          <template #default="scope">
            <div class="cell-main">
              <strong>{{ scope.row.shop?.name || '-' }}</strong>
              <span class="muted"> · {{ scope.row.dish?.name || '-' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="图片" width="120">
          <template #default="scope">
            <img v-if="scope.row.imageUrl" :src="toImageUrl(scope.row.imageUrl)" class="thumb" alt="" />
            <span v-else class="muted">-</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="120">
          <template #default>
            <el-tag type="danger">已拒绝</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </PageContainer>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import PageContainer from '../components/PageContainer.vue'
import http from '../api/http'

const items = ref([])
const loading = ref(false)
const maxPlaying = 5
const apiBase = import.meta.env.VITE_API_BASE || 'http://localhost:8080'

const playingList = computed(() => items.value.filter((i) => i.status === 'APPROVED'))
const pendingList = computed(() => items.value.filter((i) => i.status === 'PENDING'))
const rejectedList = computed(() => items.value.filter((i) => i.status === 'REJECTED'))

const load = async () => {
  loading.value = true
  try {
    items.value = await http.get('/api/carousels')
  } finally {
    loading.value = false
  }
}

const approve = async (row) => {
  if (playingList.value.length >= maxPlaying) {
    ElMessage.warning(`播放上限为 ${maxPlaying} 条，请先停止其他轮播`)
    return
  }
  await http.post(`/api/carousels/${row.id}/approve`)
  ElMessage.success('已通过')
  await load()
}

const reject = async (row) => {
  await ElMessageBox.confirm('确定拒绝该轮播申请吗？', '提示', { type: 'warning' })
  await http.post(`/api/carousels/${row.id}/reject`)
  ElMessage.success('已拒绝')
  await load()
}

const stop = async (row) => {
  await ElMessageBox.confirm('停止播放后用户端将不再展示，继续？', '提示', { type: 'warning' })
  await http.post(`/api/carousels/${row.id}/stop`)
  ElMessage.success('已停止播放')
  await load()
}

onMounted(load)

const toImageUrl = (url) => {
  if (!url) return ''
  return url.startsWith('http') ? url : `${apiBase}${url}`
}
</script>

<style scoped>
.section {
  display: grid;
  gap: var(--space-sm);
}

.section + .section {
  margin-top: var(--space-lg);
}

.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.title {
  margin: 0;
  color: var(--text-primary);
}

.muted {
  margin: 0;
  color: var(--text-muted);
}

.thumb {
  width: 80px;
  height: 48px;
  object-fit: cover;
  border-radius: var(--radius-sm);
  border: 1px solid var(--card-border);
}

.cell-main {
  display: flex;
  align-items: center;
  gap: var(--space-xs);
}

.page-table :deep(.el-table__cell) {
  padding: 10px 8px;
}
</style>
