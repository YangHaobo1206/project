<template>
  <div class="page">
    <el-page-header content="用户首页">
      <template #extra>
        <el-button type="primary" @click="goOrders">我的订单</el-button>
        <el-button @click="store.logout">退出登录</el-button>
      </template>
    </el-page-header>

    <div class="content">
      <el-empty description="这里可以展示商家列表 / 菜品列表，当前为示例占位" />
    </div>
  </div>
</template>

<script setup>
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/userStore";

const router = useRouter();
const store = useUserStore();

const goOrders = () => {
  router.push("/user/orders");
};
</script>

<style scoped>
.page {
  padding: 16px;
}
.content {
  margin-top: 16px;
}
</style>
