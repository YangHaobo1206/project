import axios from "axios";
import { ElMessage } from "element-plus";
import { useUserStore } from "@/stores/userStore";

const http = axios.create({
  baseURL: "/api"
});

http.interceptors.request.use((config) => {
  const store = useUserStore();
  if (store.token) {
    config.headers.Authorization = store.token;
  }
  return config;
});

http.interceptors.response.use(
  (res) => res.data,
  (error) => {
    const msg = error.response?.data?.message || error.message || "请求失败";
    ElMessage.error(msg);
    if (error.response?.status === 401) {
      useUserStore().logout();
    }
    return Promise.reject(error);
  }
);

export default http;
