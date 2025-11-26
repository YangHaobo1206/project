import { defineStore } from "pinia";
import { getProfileApi } from "@/api/userApi";
import router from "@/router";

export const useUserStore = defineStore("user", {
  state: () => ({
    token: localStorage.getItem("token") || "",
    profile: null
  }),
  actions: {
    setToken(token) {
      this.token = token;
      localStorage.setItem("token", token);
      this.fetchProfile();
    },
    async fetchProfile() {
      if (!this.token) return;
      const res = await getProfileApi();
      if (res.code === 200) {
        this.profile = res.data;
      }
    },
    logout() {
      this.token = "";
      this.profile = null;
      localStorage.removeItem("token");
      router.push("/login");
    }
  }
});
