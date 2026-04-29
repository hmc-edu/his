import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import router from '@/router'

const request = axios.create({
  baseURL: '/',
  timeout: 15000
})

request.interceptors.request.use(config => {
  const userStore = useUserStore()
  if (userStore.token) {
    config.headers[userStore.tokenName || 'Authorization'] = userStore.token
  }
  return config
})

request.interceptors.response.use(
  response => {
    const data = response.data
    if (data && typeof data === 'object' && 'code' in data) {
      if (data.code === 0) {
        return data.data
      }
      if (data.code === 401) {
        const userStore = useUserStore()
        userStore.clear()
        ElMessage.error(data.msg || '请先登录')
        router.replace('/login')
        return Promise.reject(new Error(data.msg))
      }
      ElMessage.error(data.msg || '操作失败')
      return Promise.reject(new Error(data.msg))
    }
    return data
  },
  error => {
    ElMessage.error(error.message || '网络异常')
    return Promise.reject(error)
  }
)

export default request
