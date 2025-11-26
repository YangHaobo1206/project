<template>
  <div class="page">
    <el-page-header content="我的订单" @back="goBack" />
    <el-table :data="orders" style="width: 100%; margin-top: 16px">
      <el-table-column prop="id" label="订单号" width="80" />
      <el-table-column prop="status" label="状态" width="120" />
      <el-table-column prop="totalAmount" label="金额" width="120" />
      <el-table-column prop="createTime" label="下单时间" />
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { listUserOrdersApi } from "@/api/orderApi";

const router = useRouter();
const orders = ref([]);

const goBack = () => {
  router.back();
};

const loadData = async () => {
  const res = await listUserOrdersApi();
  if (res.code === 200) {
    orders.value = res.data || [];
  }
};

onMounted(loadData);
</script>

<style scoped>
.page {
  padding: 16px;
}
</style>
