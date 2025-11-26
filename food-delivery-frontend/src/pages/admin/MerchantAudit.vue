<template>
  <div class="page">
    <el-page-header content="商家审核" />
    <el-table :data="list" style="width: 100%; margin-top: 16px">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="address" label="地址" />
      <el-table-column prop="phone" label="电话" />
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button size="small" type="success" @click="approve(scope.row.id, true)">通过</el-button>
          <el-button size="small" type="danger" @click="approve(scope.row.id, false)">驳回</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { listPendingMerchantsApi, approveMerchantApi } from "@/api/merchantApi";

const list = ref([]);

const loadData = async () => {
  const res = await listPendingMerchantsApi();
  if (res.code === 200) {
    list.value = res.data || [];
  }
};

const approve = async (id, pass) => {
  const res = await approveMerchantApi(id, pass);
  if (res.code === 200) {
    ElMessage.success("操作成功");
    loadData();
  } else {
    ElMessage.error(res.msg || "操作失败");
  }
};

onMounted(loadData);
</script>

<style scoped>
.page {
  padding: 16px;
}
</style>
