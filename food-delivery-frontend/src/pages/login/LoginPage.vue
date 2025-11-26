<template>
  <div class="login-wrapper">
    <el-card class="login-card">
      <h2 class="title">外卖平台登录</h2>
      <el-form :model="form" label-width="80px" @keyup.enter="handleLogin">
        <el-form-item label="账号">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleLogin">
            登录
          </el-button>
          <el-button type="text" @click="mockRegister">快速注册测试账号</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { loginApi, registerApi } from "@/api/userApi";
import { useUserStore } from "@/stores/userStore";

const router = useRouter();
const store = useUserStore();
const loading = ref(false);
const form = ref({
  username: "",
  password: ""
});

const handleLogin = async () => {
  if (!form.value.username || !form.value.password) {
    ElMessage.warning("请输入账号和密码");
    return;
  }
  loading.value = true;
  try {
    const res = await loginApi(form.value);
    if (res.code === 200) {
      store.setToken(res.data);
      ElMessage.success("登录成功");
      router.push("/user/home");
    } else {
      ElMessage.error(res.msg || "登录失败");
    }
  } finally {
    loading.value = false;
  }
};

const mockRegister = async () => {
  if (!form.value.username || !form.value.password) {
    form.value.username = "user" + Date.now().toString().slice(-4);
    form.value.password = "123456";
  }
  const res = await registerApi({
    username: form.value.username,
    password: form.value.password
  });
  if (res.code === 200) {
    ElMessage.success("注册成功，请点击登录");
  } else {
    ElMessage.error(res.msg || "注册失败");
  }
};
</script>

<style scoped>
.login-wrapper {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f2f5;
}
.login-card {
  width: 380px;
}
.title {
  text-align: center;
  margin-bottom: 20px;
}
</style>
