import { ref } from 'vue'
import { defineStore } from 'pinia'
import type { MenuItem, UserInfo } from '@/utils/api-types'
import { getMenus, getUserInfo as fetchUserInfoApi } from '@/api/auth'
import { getToken, removeToken, setToken } from '@/utils/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref<string | null>(getToken())
  const userInfo = ref<UserInfo | null>(null)
  const menus = ref<MenuItem[]>([])

  const setAuth = (newToken: string, info: UserInfo) => {
    token.value = newToken
    userInfo.value = info
    setToken(newToken)
  }

  const clearAuth = () => {
    token.value = null
    userInfo.value = null
    removeToken()
  }

  const fetchUserInfo = async () => {
    const { data } = await fetchUserInfoApi()
    userInfo.value = data
    return data
  }

  const fetchMenus = async () => {
    const { data } = await getMenus()
    menus.value = data ?? []
    return data
  }

  const initAuth = async () => {
    if (!token.value) {
      return false
    }

    try {
      await fetchUserInfo()
      await fetchMenus()
      return true
    } catch {
      clearAuth()
      return false
    }
  }

  return {
    token,
    userInfo,
    menus,
    setAuth,
    clearAuth,
    fetchUserInfo,
    fetchMenus,
    initAuth,
  }
})
