<template>
  <PageContainer title="运营概览" subtitle="每日经营快照，聚焦订单与用户">
    <div class="kpi-row">
      <div v-for="card in topCards" :key="card.title" class="kpi-card" :class="{ accent: card.accent }">
        <div class="kpi-meta">
          <div class="kpi-title">{{ card.title }}</div>
          <small class="muted">{{ card.sub }}</small>
        </div>
        <div class="kpi-value">{{ card.value }}</div>
      </div>
    </div>

    <div class="chart-grid">
      <div class="chart-panel">
        <div class="panel-title">订单状态分布</div>
        <div ref="orderStatusRef" class="chart mid"></div>
      </div>
      <div class="chart-panel">
        <div class="panel-title">菜品分类分布</div>
        <div ref="dishChartRef" class="chart mid"></div>
      </div>
    </div>

    <div class="chart-panel trend-panel">
      <div class="panel-title">近7日订单趋势</div>
      <div ref="orderDateRef" class="chart low"></div>
    </div>
  </PageContainer>
</template>

<script setup>
import { computed, onMounted, onUnmounted, reactive, ref } from 'vue'
import * as echarts from 'echarts'
import PageContainer from '../components/PageContainer.vue'
import http from '../api/http'

const stats = reactive({
  userCount: 0,
  dishStats: [],
  orderStats: [],
  orderByDate: [],
  totalOrders: 0,
  last7Days: 0
})

const dishChartRef = ref(null)
const orderStatusRef = ref(null)
const orderDateRef = ref(null)
let dishChart, orderStatusChart, orderDateChart

const pendingOrders = computed(() =>
  stats.orderStats
    .filter((d) => d.name === 'CREATED' || d.name === 'PAID')
    .reduce((s, i) => s + Number(i.value || 0), 0)
)

const topCards = computed(() => [
  { title: '订单总数', sub: '所有状态合计', value: stats.totalOrders, accent: true },
  { title: '近7日订单', sub: '近七天订单总量', value: stats.last7Days, accent: false },
  { title: '待处理订单', sub: '未完成 / 未接单', value: pendingOrders.value, accent: false }
])

const renderCharts = () => {
  if (orderStatusRef.value) {
    orderStatusChart = echarts.init(orderStatusRef.value)
    orderStatusChart.setOption({
      tooltip: {},
      xAxis: { type: 'category', data: stats.orderStats.map((d) => d.name) },
      yAxis: { type: 'value' },
      series: [{ type: 'bar', data: stats.orderStats.map((d) => d.value), itemStyle: { color: '#ff8a00' } }]
    })
  }
  if (dishChartRef.value) {
    dishChart = echarts.init(dishChartRef.value)
    dishChart.setOption({
      tooltip: { trigger: 'item' },
      series: [
        {
          type: 'pie',
          radius: ['42%', '70%'],
          data: stats.dishStats.map((d) => ({ name: d.name, value: d.value })),
          label: { formatter: '{b}: {c}' }
        }
      ]
    })
  }
  if (orderDateRef.value) {
    orderDateChart = echarts.init(orderDateRef.value)
    orderDateChart.setOption({
      tooltip: {},
      xAxis: { type: 'category', data: stats.orderByDate.map((d) => d.name) },
      yAxis: { type: 'value' },
      series: [
        {
          type: 'line',
          data: stats.orderByDate.map((d) => d.value),
          smooth: true,
          areaStyle: { color: 'rgba(255,138,0,0.12)' },
          lineStyle: { color: '#ff8a00' }
        }
      ]
    })
  }
}

const load = async () => {
  const res = await http.get('/api/dashboard/admin')
  stats.userCount = res.userCount || 0
  stats.dishStats = res.dishStats || []
  stats.orderStats = res.orderStats || []
  stats.orderByDate = res.orderByDate || []
  stats.totalOrders = stats.orderStats.reduce((s, i) => s + Number(i.value || 0), 0)
  stats.last7Days = stats.orderByDate.reduce((s, i) => s + Number(i.value || 0), 0)
  renderCharts()
}

onMounted(() => {
  load()
  window.addEventListener('resize', resize)
})

const resize = () => {
  dishChart?.resize()
  orderStatusChart?.resize()
  orderDateChart?.resize()
}

onUnmounted(() => {
  window.removeEventListener('resize', resize)
  dishChart?.dispose()
  orderStatusChart?.dispose()
  orderDateChart?.dispose()
})
</script>

<style scoped>
.kpi-row {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: var(--space-md);
  margin-bottom: var(--space-md);
}

.kpi-card {
  padding: var(--space-md);
  border: 1px solid var(--card-border);
  border-radius: var(--radius-lg);
  background: #fffcf5;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.kpi-card.accent {
  background: #fff4dd;
  border-color: #ffd8a8;
}

.kpi-meta {
  display: grid;
  gap: var(--space-xs);
}

.kpi-title {
  font-weight: 600;
}

.kpi-value {
  font-size: 28px;
  font-weight: 800;
  color: #d46b08;
}

.chart-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: var(--space-md);
  margin-bottom: var(--space-md);
}

.chart-panel {
  padding: var(--space-md);
  border: 1px solid var(--card-border);
  border-radius: var(--radius-lg);
  background: var(--card-bg);
}

.trend-panel {
  padding: var(--space-md);
  border: 1px solid var(--card-border);
  border-radius: var(--radius-lg);
  background: var(--card-bg);
}

.panel-title {
  font-weight: 600;
  margin-bottom: 8px;
}

.chart {
  width: 100%;
  height: 240px;
}

.chart.mid {
  height: 260px;
}

.chart.low {
  height: 220px;
}
</style>
