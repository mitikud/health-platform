import axios from 'axios'
import { useAuth } from '../store/auth'


const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api'
})

// attach JWT if any
api.interceptors.request.use((config) => {
  const { token, role } = useAuth.getState()
  if (token) config.headers.Authorization = `Bearer ${token}`
  if (role)  config.headers['X-User-Role'] = role
  return config
})

export default api
