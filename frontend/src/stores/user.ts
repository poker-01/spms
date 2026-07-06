import { ref } from 'vue'
import { defineStore } from 'pinia'
import type { MenuItem, UserInfo } from '@/utils/api-types'
import { getMenus, getUserInfo as fetchUserInfoApi } from '@/api/auth'
import { getToken, removeToken, setToken } from '@/utils/auth'

const collectPermissionCodes = (menus: MenuItem[]): string[] => {
  const codes: string[] = []
  const walk = (items: MenuItem[]) => {
    for (const item of items) {
      if (item.permissionCode) {
        codes.push(item.permissionCode)
      }
      if (item.children && item.children.length > 0) {
        walk(item.children)
      }
    }
  }
  walk(menus)
  return codes
}

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
    const menuList = data ?? []
    menus.value = menuList

    // 当后端 permission_str 为空时，用菜单 permissionCode 补齐权限列表
    if (userInfo.value) {
      const menuPermissions = collectPermissionCodes(menuList)
      const existing = new Set(userInfo.value.permissions ?? [])
      menuPermissions.forEach((code) => existing.add(code))
      userInfo.value.permissions = Array.from(existing)
    }

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
