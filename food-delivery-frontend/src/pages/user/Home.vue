<template>
  <div class="page">
    <el-page-header content="用户首页">
      <template #extra>
        <el-button type="primary" @click="goOrders">我的订单</el-button>
        <el-button @click="store.logout">退出登录</el-button>
      </template>
    </el-page-header>

    <el-row :gutter="16" class="content">
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>可下单商家</template>
          <el-menu :default-active="activeMerchantId" @select="onMerchantSelect">
            <el-menu-item v-for="m in merchants" :key="m.id" :index="String(m.id)">
              {{ m.name }}
            </el-menu-item>
          </el-menu>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>菜品列表</template>
          <el-table :data="dishes" style="width: 100%">
            <el-table-column prop="name" label="名称" />
            <el-table-column prop="category" label="分类" />
            <el-table-column prop="price" label="价格" />
            <el-table-column label="操作">
              <template #default="scope">
                <el-button size="small" type="primary" @click="orderDish(scope.row)">
                  下单
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { useUserStore } from "@/stores/userStore";
import { listApprovedMerchantsApi } from "@/api/merchantApi";
import { listOnSaleByMerchantApi } from "@/api/dishApi";
import { createOrderApi } from "@/api/orderApi";

const router = useRouter();
const store = useUserStore();
const merchants = ref([]);
const dishes = ref([]);
const activeMerchantId = ref("");

const loadMerchants = async () => {
  const res = await listApprovedMerchantsApi();
  if (res.code === 200) {
    merchants.value = res.data;
    if (res.data?.length) {
      onMerchantSelect(String(res.data[0].id));
    }
  }
};

const onMerchantSelect = async (id) => {
  activeMerchantId.value = id;
  const res = await listOnSaleByMerchantApi(id);
  dishes.value = res.code === 200 ? res.data : [];
};

const orderDish = async (dish) => {
  const res = await createOrderApi({
    merchantId: dish.merchantId,
    items: [{ dishId: dish.id, quantity: 1 }]
  });
  if (res.code === 200) {
    ElMessage.success("下单成功");
  }
};

const goOrders = () => {
  router.push("/user/orders");
};

onMounted(loadMerchants);
</script>

<style scoped>
.page {
  padding: 16px;
}
.content {
  margin-top: 16px;
}
</style>
