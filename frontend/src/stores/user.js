import { defineStore } from 'pinia'
import { loginApi, logoutApi, meApi } from '@/api/auth'

const STORAGE_KEY = 'his_auth'

function loadFromStorage() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    return raw ? JSON.parse(raw) : null
  } catch (e) {
    return null
  }
}

export const useUserStore = defineStore('user', {
  state: () => {
    const cached = loadFromStorage()
    return {
      tokenName: cached?.tokenName || 'Authorization',
      token: cached?.token || '',
      user: cached?.user || null
    }
  },
  getters: {
    isLogin: (s) => !!s.token,
    role: (s) => s.user?.role || ''
  },
  actions: {
    persist() {
      localStorage.setItem(STORAGE_KEY, JSON.stringify({
        tokenName: this.tokenName,
        token: this.token,
        user: this.user
      }))
    },
    clear() {
      this.token = ''
      this.user = null
      localStorage.removeItem(STORAGE_KEY)
    },
    async login(payload) {
      const res = await loginApi(payload)
      this.tokenName = res.tokenName
      this.token = res.tokenValue
      this.user = res.userInfo
      this.persist()
      return res
    },
    async fetchMe() {
      const info = await meApi()
      this.user = info
      this.persist()
      return info
    },
    async logout() {
      try { await logoutApi() } catch (_) {}
      this.clear()
    }
  }
})
