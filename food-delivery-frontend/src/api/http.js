import axios from "axios";
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
  (error) => Promise.reject(error)
);

export default http;
