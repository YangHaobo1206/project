import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))

  const setAuth = (payload) => {
    token.value = payload.token
    user.value = payload.user
    localStorage.setItem('token', token.value)
    localStorage.setItem('user', JSON.stringify(user.value))
  }

  const logout = () => {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  return { token, user, setAuth, logout }
})
