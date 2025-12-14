import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Dashboard from '../views/Dashboard.vue'
import AdminDashboardPage from '../views/AdminDashboardPage.vue'
import Users from '../views/Users.vue'
import Shops from '../views/Shops.vue'
import Dishes from '../views/Dishes.vue'
import Orders from '../views/Orders.vue'
import Carousels from '../views/Carousels.vue'
import ClientDashboard from '../views/ClientDashboard.vue'
import ClientShops from '../views/ClientShops.vue'
import ClientDishes from '../views/ClientDishes.vue'
import ClientOrders from '../views/ClientOrders.vue'
import ClientProfile from '../views/ClientProfile.vue'
import MerchantDashboard from '../views/MerchantDashboard.vue'
import MerchantDashboardPage from '../views/MerchantDashboardPage.vue'
import MerchantOrders from '../views/MerchantOrders.vue'
import NotFound from '../views/NotFound.vue'
import { useAuthStore } from '../store/auth'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  {
    path: '/admin',
    component: Dashboard,
    children: [
      { path: '', redirect: '/admin/dashboard' },
      { path: 'dashboard', component: AdminDashboardPage },
      { path: 'users', component: Users },
      { path: 'shops', component: Shops },
      { path: 'dishes', component: Dishes },
      { path: 'orders', component: Orders },
      { path: 'carousels', component: Carousels }
    ]
  },
  {
    path: '/client',
    component: ClientDashboard,
    children: [
      { path: '', redirect: '/client/shops' },
      { path: 'shops', component: ClientShops },
      { path: 'dishes', component: ClientDishes },
      { path: 'orders', component: ClientOrders },
      { path: 'profile', component: ClientProfile }
    ]
  },
  {
    path: '/merchant',
    component: MerchantDashboard,
    children: [
      { path: '', redirect: '/merchant/dashboard' },
      { path: 'dashboard', component: MerchantDashboardPage },
      { path: 'dishes', component: Dishes },
      { path: 'orders', component: MerchantOrders }
    ]
  },
  { path: '/:pathMatch(.*)*', name: 'NotFound', component: NotFound }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const auth = useAuthStore()
  const publicPaths = ['/login', '/register']
  const isAuthed = !!auth.token
  const role = auth.user?.role

  if (!isAuthed && !publicPaths.includes(to.path)) {
    next('/login')
    return
  }

  if (to.path.startsWith('/admin') && role !== 'ADMIN') {
    ElMessage.warning('仅管理员可访问后台')
    next('/client/shops')
    return
  }

  if (to.path.startsWith('/merchant')) {
    if (role === 'MERCHANT') {
      next()
      return
    }
    ElMessage.warning('仅商家可访问商家端')
    next(role === 'ADMIN' ? '/admin/users' : '/client/shops')
    return
  }

  if (to.path.startsWith('/client')) {
    if (role === 'ADMIN') {
      next('/admin/users')
      return
    }
  }

  next()
})

export default router
