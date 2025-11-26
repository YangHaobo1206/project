<template>
  <div class="page">
    <el-page-header content="菜品管理">
      <template #extra>
        <el-button type="primary" @click="showAdd = true">新增菜品</el-button>
      </template>
    </el-page-header>
    <el-table :data="dishes" style="margin-top: 16px">
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="category" label="分类" />
      <el-table-column prop="price" label="价格" />
      <el-table-column prop="status" label="状态" />
      <el-table-column label="操作">
        <template #default="scope">
          <el-button size="small" @click="editDish(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="removeDish(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="showAdd" title="菜品信息" width="400">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="分类">
          <el-input v-model="form.category" />
        </el-form-item>
        <el-form-item label="价格">
          <el-input v-model.number="form.price" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status">
            <el-option label="上架" value="ON" />
            <el-option label="下架" value="OFF" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAdd = false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { ElMessage } from "element-plus";
import {
  addDishApi,
  deleteDishApi,
  listMerchantDishesApi,
  updateDishApi
} from "@/api/dishApi";
import { getMyMerchantApi } from "@/api/merchantApi";

const dishes = ref([]);
const merchantId = ref(null);
const showAdd = ref(false);
const form = ref({ name: "", category: "", price: 0, status: "ON" });

const loadData = async () => {
  const merchantRes = await getMyMerchantApi();
  merchantId.value = merchantRes.data?.id;
  if (merchantId.value) {
    const res = await listMerchantDishesApi(merchantId.value);
    dishes.value = res.code === 200 ? res.data : [];
  }
};

const submitForm = async () => {
  const payload = { ...form.value };
  if (payload.id) {
    await updateDishApi(payload);
    ElMessage.success("更新成功");
  } else {
    await addDishApi(payload);
    ElMessage.success("创建成功");
  }
  showAdd.value = false;
  form.value = { name: "", category: "", price: 0, status: "ON" };
  loadData();
};

const editDish = (row) => {
  form.value = { ...row };
  showAdd.value = true;
};

const removeDish = async (id) => {
  await deleteDishApi(id);
  ElMessage.success("删除成功");
  loadData();
};

onMounted(loadData);
</script>

<style scoped>
.page {
  padding: 16px;
}
</style>
