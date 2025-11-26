import { createRouter, createWebHistory } from "vue-router";
import { useUserStore } from "@/stores/userStore";

const LoginPage = () => import("@/pages/login/LoginPage.vue");
const UserHome = () => import("@/pages/user/Home.vue");
const UserOrders = () => import("@/pages/user/Orders.vue");
const UserProfile = () => import("@/pages/user/Profile.vue");

const MerchantDashboard = () => import("@/pages/merchant/Dashboard.vue");
const MerchantDishManage = () => import("@/pages/merchant/DishManage.vue");
const MerchantOrderManage = () => import("@/pages/merchant/OrderManage.vue");

const AdminDashboard = () => import("@/pages/admin/Dashboard.vue");
const AdminMerchantAudit = () => import("@/pages/admin/MerchantAudit.vue");

const NotFound = () => import("@/pages/error/NotFound.vue");
const Forbidden = () => import("@/pages/error/Forbidden.vue");

const routes = [
  { path: "/login", name: "login", component: LoginPage },
  {
    path: "/",
    redirect: "/user/home"
  },
  {
    path: "/user",
    children: [
      { path: "home", name: "user-home", component: UserHome },
      { path: "orders", name: "user-orders", component: UserOrders },
      { path: "profile", name: "user-profile", component: UserProfile }
    ]
  },
  {
    path: "/merchant",
    children: [
      { path: "dashboard", name: "merchant-dashboard", component: MerchantDashboard },
      { path: "dishes", name: "merchant-dishes", component: MerchantDishManage },
      { path: "orders", name: "merchant-orders", component: MerchantOrderManage }
    ]
  },
  {
    path: "/admin",
    children: [
      { path: "dashboard", name: "admin-dashboard", component: AdminDashboard },
      { path: "merchant-audit", name: "admin-merchant-audit", component: AdminMerchantAudit }
    ]
  },
  { path: "/403", name: "forbidden", component: Forbidden },
  { path: "/:pathMatch(.*)*", name: "not-found", component: NotFound }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

router.beforeEach((to, from, next) => {
  const publicPages = ["/login"];
  const store = useUserStore();
  if (publicPages.includes(to.path)) {
    return next();
  }
  if (!store.token) {
    return next("/login");
  }
  next();
});

export default router;
