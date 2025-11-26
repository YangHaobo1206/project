<template>
  <div class="page">
    <el-page-header content="个人中心" />
    <el-card class="card">
      <el-form :model="form" label-width="80px">
        <el-form-item label="账号">
          <el-input v-model="form.username" disabled />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="form.nickname" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="save">保存</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, watch } from "vue";
import { ElMessage } from "element-plus";
import { useUserStore } from "@/stores/userStore";
import { updateProfileApi } from "@/api/userApi";

const store = useUserStore();
const form = reactive({
  username: "",
  nickname: "",
  phone: ""
});

watch(
  () => store.profile,
  (p) => {
    if (p) {
      form.username = p.username;
      form.nickname = p.nickname || "";
      form.phone = p.phone || "";
    }
  },
  { immediate: true }
);

const save = async () => {
  const res = await updateProfileApi({
    nickname: form.nickname,
    phone: form.phone
  });
  if (res.code === 200) {
    ElMessage.success("保存成功");
    store.fetchProfile();
  } else {
    ElMessage.error(res.msg || "保存失败");
  }
};
</script>

<style scoped>
.page {
  padding: 16px;
}
.card {
  max-width: 480px;
  margin-top: 16px;
}
</style>
