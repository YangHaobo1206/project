import axios from 'axios'
import { useAuthStore } from '../store/auth'

const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || 'http://localhost:8080'
})

http.interceptors.request.use((config) => {
  const auth = useAuthStore()
  if (auth.token) {
    config.headers.Authorization = `Bearer ${auth.token}`
  }
  return config
})

http.interceptors.response.use(
  (response) => {
    const payload = response.data
    if (payload && typeof payload.code !== 'undefined') {
      if (payload.code !== 0) {
        return Promise.reject(payload)
      }
      return payload.data
    }
    return payload
  },
  (error) => {
    const auth = useAuthStore()
    if (error.response && error.response.status === 401) {
      auth.logout()
    }
    return Promise.reject(error)
  }
)

export default http
