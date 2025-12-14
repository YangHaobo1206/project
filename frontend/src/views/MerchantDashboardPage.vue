<template>
  <PageContainer title="经营概览" subtitle="每日经营快照，聚焦订单与营收">
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
        <div ref="statusPieRef" class="chart mid"></div>
      </div>
      <div class="chart-panel">
        <div class="panel-title">菜品销量概览</div>
        <div ref="statusBarRef" class="chart mid"></div>
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
  orderStats: [],
  orderByDate: [],
  totalOrders: 0,
  last7Days: 0
})

const statusPieRef = ref(null)
const statusBarRef = ref(null)
const orderDateRef = ref(null)
let statusPieChart, statusBarChart, orderDateChart

const pendingOrders = computed(() =>
  stats.orderStats.filter((d) => d.name === 'CREATED' || d.name === 'PAID').reduce((s, i) => s + Number(i.value || 0), 0)
)

const topCards = computed(() => [
  { title: '总订单', sub: '当前全部订单数', value: stats.totalOrders, accent: true },
  { title: '近7日订单', sub: '近7天订单合计', value: stats.last7Days, accent: false },
  { title: '待处理订单', sub: '未完成 / 未接单', value: pendingOrders.value, accent: false }
])

const renderCharts = () => {
  if (statusPieRef.value) {
    statusPieChart = echarts.init(statusPieRef.value)
    statusPieChart.setOption({
      tooltip: { trigger: 'item' },
      series: [
        {
          type: 'pie',
          radius: ['42%', '70%'],
          data: stats.orderStats.map((d) => ({ name: d.name, value: d.value })),
          label: { formatter: '{b}: {c}' }
        }
      ]
    })
  }
  if (statusBarRef.value) {
    statusBarChart = echarts.init(statusBarRef.value)
    statusBarChart.setOption({
      tooltip: {},
      xAxis: { type: 'category', data: stats.orderStats.map((d) => d.name) },
      yAxis: { type: 'value' },
      series: [{ type: 'bar', data: stats.orderStats.map((d) => d.value), itemStyle: { color: '#ff8a00' } }]
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
  const res = await http.get('/api/dashboard/merchant')
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
  statusPieChart?.resize()
  statusBarChart?.resize()
  orderDateChart?.resize()
}

onUnmounted(() => {
  window.removeEventListener('resize', resize)
  statusPieChart?.dispose()
  statusBarChart?.dispose()
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
  background: #fff7ed;
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
