import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Dashboard from '../views/Dashboard.vue'
import Users from '../views/Users.vue'
import Shops from '../views/Shops.vue'
import Dishes from '../views/Dishes.vue'
import Orders from '../views/Orders.vue'
import NotFound from '../views/NotFound.vue'
import { useAuthStore } from '../store/auth'

const routes = [
  { path: '/login', component: Login },
  {
    path: '/',
    component: Dashboard,
    children: [
      { path: '', redirect: '/users' },
      { path: '/users', component: Users },
      { path: '/shops', component: Shops },
      { path: '/dishes', component: Dishes },
      { path: '/orders', component: Orders }
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
  if (to.path !== '/login' && !auth.token) {
    next('/login')
  } else {
    next()
  }
})

export default router
