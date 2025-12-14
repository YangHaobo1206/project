<template>
  <PageContainer title="菜品管理" subtitle="上架、价格及描述">
    <template #actions>
      <el-button type="primary" size="small" @click="openCreate">新增菜品</el-button>
      <el-button size="small" class="btn-soft" @click="load">刷新</el-button>
    </template>

    <el-alert
      v-if="isMerchant"
      :title="merchantShop ? `当前店铺：${merchantShop.name}（${statusText(merchantShop.status)}）` : '暂无店铺'"
      :type="canManage ? 'success' : 'warning'"
      :closable="false"
      style="margin-bottom: 10px"
    />

    <el-table :data="dishes" class="page-table" :loading="loading" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="price" label="价格" />
      <el-table-column prop="category" label="分类" />
      <el-table-column label="图片" width="120">
        <template #default="scope">
          <img
            v-if="scope.row.imageUrl"
            :src="toImageUrl(scope.row.imageUrl)"
            alt=""
            style="width: 64px; height: 64px; object-fit: cover;"
          />
          <span v-else class="muted">无</span>
        </template>
      </el-table-column>
      <el-table-column label="所属商家">
        <template #default="scope">
          {{ scope.row.shop?.name || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="店铺审核" width="120">
        <template #default="scope">
          <el-tag :type="statusTag(scope.row.shop?.status)">
            {{ statusText(scope.row.shop?.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="120">
        <template #default="scope">
          <el-tag :type="scope.row.available ? 'success' : 'info'">
            {{ scope.row.available ? '上架' : '下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="轮播状态" width="140">
        <template #default="scope">
          <el-tag :type="carouselTag(scope.row)">{{ carouselStatusText(scope.row) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="340">
        <template #default="scope">
          <el-button size="small" :disabled="!rowCanOperate(scope.row)" @click="openEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="warning" :disabled="!rowCanOperate(scope.row)" @click="toggle(scope.row)">
            {{ scope.row.available ? '下架' : '上架' }}
          </el-button>
          <el-button size="small" type="danger" :disabled="!rowCanOperate(scope.row)" @click="remove(scope.row)">删除</el-button>
          <el-button
            v-if="isMerchant"
            size="small"
            type="primary"
            plain
            :disabled="!canApplyCarousel(scope.row)"
            @click="applyCarousel(scope.row)"
          >
            申请轮播
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </PageContainer>

  <el-dialog v-model="dialogVisible" :title="form.id ? '编辑菜品' : '新增菜品'" width="500px">
    <el-form :model="form" label-width="90px">
      <el-form-item label="名称" required>
        <el-input v-model="form.name" />
      </el-form-item>
      <el-form-item label="价格" required>
        <el-input v-model.number="form.price" type="number" min="0" />
      </el-form-item>
      <el-form-item label="分类">
        <el-input v-model="form.category" />
      </el-form-item>
      <el-form-item label="图片">
        <el-upload
          :action="uploadAction"
          :show-file-list="false"
          :headers="uploadHeaders"
          :on-success="handleUploadSuccess"
          :before-upload="beforeUpload"
        >
          <el-button type="primary" plain>上传图片</el-button>
        </el-upload>
        <div v-if="form.imageUrl" style="margin-top: 8px;">
          <img
            :src="toImageUrl(form.imageUrl)"
            alt=""
            style="width: 96px; height: 96px; object-fit: cover; border-radius: 6px;"
          />
        </div>
      </el-form-item>
      <el-form-item v-if="!isMerchant" label="商家" required>
        <el-select v-model="form.shopId" placeholder="选择商家">
          <el-option v-for="shop in shopsForSelect" :key="shop.id" :label="shop.name" :value="shop.id" />
        </el-select>
      </el-form-item>
      <el-form-item v-else label="商家">
        <el-input :model-value="merchantShop?.name || '待审核'" disabled />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="saving" @click="save">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import PageContainer from '../components/PageContainer.vue'
import http from '../api/http'
import { useAuthStore } from '../store/auth'

const dishes = ref([])
const shops = ref([])
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const auth = useAuthStore()
const isMerchant = computed(() => auth.user?.role === 'MERCHANT')
const carouselMap = ref({})
const form = reactive({
  id: null,
  name: '',
  price: 0,
  category: '',
  shopId: null,
  imageUrl: ''
})
const shopsForSelect = computed(() =>
  isMerchant.value ? shops.value : (shops.value || []).filter((s) => s.status === 'APPROVED')
)
const merchantShop = computed(() => (isMerchant.value ? shops.value[0] : null))
const canManage = computed(() => {
  if (!isMerchant.value) return true
  if (!merchantShop.value) return false
  return merchantShop.value.status === 'APPROVED'
})
const statusText = (status) => {
  if (status === 'APPROVED') return '审核通过'
  if (status === 'REJECTED') return '已拒绝'
  return '待审核'
}
const statusTag = (status) => {
  if (status === 'APPROVED') return 'success'
  if (status === 'REJECTED') return 'danger'
  return 'warning'
}
const rowCanOperate = (row) => {
  if (isMerchant.value) return canManage.value
  return row?.shop?.status === 'APPROVED'
}
const uploadAction = computed(() => {
  if (!form.id) return ''
  const base = import.meta.env.VITE_API_BASE || 'http://localhost:8080'
  return `${base}/api/dishes/${form.id}/image`
})
const apiBase = import.meta.env.VITE_API_BASE || 'http://localhost:8080'
const uploadHeaders = computed(() => {
  if (!auth.token) return {}
  return { Authorization: `Bearer ${auth.token}` }
})
const handleUploadSuccess = (res) => {
  if (res && res.code === 0) {
    form.imageUrl = res.data
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(res?.message || '上传失败')
  }
}
const beforeUpload = () => {
  if (!form.id) {
    ElMessage.warning('请先保存菜品后再上传图片')
    return false
  }
  return true
}
const toImageUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return `${apiBase}${url}`
}

const resetForm = () => {
  form.id = null
  form.name = ''
  form.price = 0
  form.category = ''
  form.shopId = null
  form.imageUrl = ''
}

const loadCarouselStatus = async () => {
  try {
    const url = isMerchant.value ? '/api/carousels/mine' : '/api/carousels'
    const list = await http.get(url)
    const map = {}
    ;(list || []).forEach((item) => {
      const did = item.dishId || item.dish?.id
      if (did) map[did] = item.status
    })
    carouselMap.value = map
  } catch (error) {
    ElMessage.error(error?.message || '获取轮播状态失败')
  }
}

const carouselStatusText = (row) => {
  const status = carouselMap.value[row.id]
  if (!status) return '未申请'
  if (status === 'PENDING') return '待审核'
  if (status === 'APPROVED') return '播放中'
  if (status === 'REJECTED') return '已拒绝'
  if (status === 'STOPPED') return '已下架'
  return status
}

const carouselTag = (row) => {
  const status = carouselMap.value[row.id]
  if (!status) return 'info'
  if (status === 'PENDING') return 'warning'
  if (status === 'APPROVED') return 'success'
  if (status === 'REJECTED') return 'danger'
  if (status === 'STOPPED') return 'info'
  return 'info'
}

const canApplyCarousel = (row) => {
  if (!isMerchant.value || !canManage.value) return false
  const status = carouselMap.value[row.id]
  return status !== 'PENDING' && status !== 'APPROVED'
}

const applyCarousel = async (row) => {
  if (!canApplyCarousel(row)) return
  try {
    await http.post('/api/carousels/apply', {
      shopId: merchantShop.value?.id,
      dishId: row.id
    })
    ElMessage.success('已提交轮播申请')
    await loadCarouselStatus()
  } catch (error) {
    ElMessage.error(error?.message || '申请失败')
  }
}

const load = async () => {
  loading.value = true
  try {
    if (isMerchant.value) {
      const myShop = await http.get('/api/shops/mine')
      shops.value = myShop || []
      const sid = merchantShop.value?.id
      dishes.value = sid ? await http.get(`/api/dishes/shop/${sid}`) : []
    } else {
      const [shopList, dishList] = await Promise.all([http.get('/api/shops'), http.get('/api/dishes')])
      shops.value = shopList
      dishes.value = dishList
      dishes.value = dishes.value.map((d) => ({
        ...d,
        available: d.available && d.shop?.status === 'APPROVED'
      }))
    }
    await loadCarouselStatus()
  } finally {
    loading.value = false
  }
}

const openCreate = () => {
  resetForm()
  if (isMerchant.value && merchantShop.value) {
    form.shopId = merchantShop.value.id
  } else if (isMerchant.value && !merchantShop.value) {
    ElMessage.warning('暂无店铺，无法添加菜品')
    return
  }
  dialogVisible.value = true
}

const openEdit = (row) => {
  form.id = row.id
  form.name = row.name
  form.price = Number(row.price) || 0
  form.category = row.category || ''
  form.shopId = row.shop?.id || null
  form.imageUrl = row.imageUrl || ''
  dialogVisible.value = true
}

const save = async () => {
  if (saving.value) return
  if (!form.name || (!form.shopId && !isMerchant.value)) {
    ElMessage.warning('请完整填写菜品信息')
    return
  }
  if (isMerchant.value && !canManage.value) {
    ElMessage.warning('店铺未通过审核，暂不能操作菜品')
    return
  }
  saving.value = true
  try {
    const payload = {
      name: form.name,
      category: form.category,
      price: Number(form.price),
      imageUrl: form.imageUrl
    }
    const shopId = isMerchant.value ? merchantShop.value?.id : form.shopId
    if (!shopId) {
      throw new Error('请选择店铺')
    }
    if (form.id) {
      await http.put(`/api/dishes/${form.id}`, payload, { params: { shopId } })
      ElMessage.success('更新成功')
    } else {
      await http.post('/api/dishes', payload, { params: { shopId } })
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    await load()
  } catch (error) {
    ElMessage.error(error?.message || '操作失败')
  } finally {
    saving.value = false
  }
}

const toggle = async (row) => {
  if (isMerchant.value && !canManage.value) {
    ElMessage.warning('店铺未通过审核，暂不能操作菜品')
    return
  }
  if (!isMerchant.value && row.shop?.status !== 'APPROVED') {
    ElMessage.warning('店铺未审核通过，无法切换状态')
    return
  }
  const action = row.available ? 'off' : 'on'
  await http.post(`/api/dishes/${row.id}/${action}`)
  ElMessage.success(row.available ? '已下架' : '已上架')
  await load()
}

const remove = async (row) => {
  if (isMerchant.value && !canManage.value) {
    ElMessage.warning('店铺未通过审核，暂不能操作菜品')
    return
  }
  if (!isMerchant.value && row.shop?.status !== 'APPROVED') {
    ElMessage.warning('店铺未审核通过，无法删除')
    return
  }
  await ElMessageBox.confirm(`确定删除菜品 ${row.name} 吗？`, '提示', { type: 'warning' })
  await http.delete(`/api/dishes/${row.id}`)
  ElMessage.success('删除成功')
  await load()
}

onMounted(load)
</script>

<style scoped>
.page-table :deep(.el-table__cell) {
  padding: 10px 8px;
}
</style>
