<template>
  <div class="page">
    <el-page-header content="订单管理" />
    <el-table :data="orders" style="margin-top: 16px">
      <el-table-column prop="id" label="订单号" />
      <el-table-column prop="userId" label="用户" />
      <el-table-column prop="amount" label="金额" />
      <el-table-column prop="status" label="状态" />
      <el-table-column label="操作">
        <template #default="scope">
          <el-button size="small" @click="markStatus(scope.row, 'FINISHED')">完成</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { listMerchantOrdersApi, updateOrderStatusApi } from "@/api/orderApi";
import { getMyMerchantApi } from "@/api/merchantApi";
import { ElMessage } from "element-plus";

const orders = ref([]);
const merchantId = ref(null);

const loadData = async () => {
  const merchantRes = await getMyMerchantApi();
  merchantId.value = merchantRes.data?.id;
  if (merchantId.value) {
    const res = await listMerchantOrdersApi(merchantId.value);
    orders.value = res.code === 200 ? res.data : [];
  }
};

const markStatus = async (row, status) => {
  await updateOrderStatusApi(row.id, status);
  ElMessage.success("状态已更新");
  loadData();
};

onMounted(loadData);
</script>

<style scoped>
.page {
  padding: 16px;
}
</style>
