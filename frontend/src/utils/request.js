import axios from 'axios'
import { message } from 'antd'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 可以在这里添加token等
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    // 401 未授权，跳转登录
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      // 避免在登录页循环跳转
      if (!window.location.pathname.includes('/login')) {
        window.location.href = '/login'
      }
      return Promise.reject(error)
    }
    
    // 其他错误显示提示
    const errorMsg = error.response?.data?.message || '请求失败'
    message.error(errorMsg)
    return Promise.reject(error)
  }
)

export default request
