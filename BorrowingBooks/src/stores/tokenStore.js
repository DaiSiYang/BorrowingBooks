// stores/token.ts
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useTokenStore = defineStore('token', () => {
  // 状态定义
  const token = ref(localStorage.getItem('token') || '')

  // 获取 token（可选封装成 computed，也可以直接访问 token.value）
  const getToken = computed(() => token.value)

  // 设置 token
  function setToken(newToken) {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  // 清除 token
  function clearToken() {
    token.value = ''
    localStorage.removeItem('token')
  }

  return {
    token,
    getToken,
    setToken,
    clearToken
  }
})