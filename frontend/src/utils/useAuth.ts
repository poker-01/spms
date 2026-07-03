import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { login as loginApi, logout as logoutApi } from '@/api/auth'
import type { LoginParams } from '@/utils/api-types'
import { getDefaultHomePath } from '@/utils/role'
import { useUserStore } from '@/stores/user'
import { RequestError } from '@/utils/request'

export const useAuth = () => {
  const router = useRouter()
  const userStore = useUserStore()
  const loading = ref(false)
  const errorMessage = ref('')

  const handleAuthSuccess = async (redirect?: string) => {
    const fallback = getDefaultHomePath(userStore.userInfo?.roles)
    await router.replace(redirect || fallback)
  }

  const login = async (params: LoginParams, redirect?: string) => {
    loading.value = true
    errorMessage.value = ''

    try {
      const { data } = await loginApi(params)
      userStore.setAuth(data.token, data.userInfo)
      await userStore.fetchMenus()
      await handleAuthSuccess(redirect)
    } catch (error) {
      errorMessage.value = error instanceof RequestError ? error.message : '登录失败，请稍后重试'
      throw error
    } finally {
      loading.value = false
    }
  }

  const logout = async () => {
    loading.value = true

    try {
      await logoutApi()
    } catch {
      // 即使后端登出失败，也清除本地登录态
    } finally {
      userStore.clearAuth()
      loading.value = false
      await router.replace('/login')
    }
  }

  return {
    loading,
    errorMessage,
    login,
    logout,
  }
}
