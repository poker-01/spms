import { useUserStore } from '@/stores/user'

export const hasPermission = (permission: string): boolean => {
  const userStore = useUserStore()
  const permissions = userStore.userInfo?.permissions ?? []
  return permissions.includes(permission)
}

export const hasAnyPermission = (permissions: string[]): boolean => {
  return permissions.some((p) => hasPermission(p))
}
