export type LoginType = 'owner' | 'property'

export const PROPERTY_ROLES = ['ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_REPAIR'] as const
export const OWNER_ROLES = ['ROLE_OWNER'] as const

export const ROLE_LABELS: Record<string, string> = {
  ROLE_SUPER_ADMIN: '超级管理员',
  ROLE_ADMIN: '物业管理员',
  ROLE_OWNER: '业主',
  ROLE_REPAIR: '维修人员',
}

export const formatRoleLabels = (roles: string[] = []) => {
  return roles.map((role) => ROLE_LABELS[role] || role).join('、')
}

export const isPropertyUser = (roles: string[] = []) => {
  return roles.some((role) => (PROPERTY_ROLES as readonly string[]).includes(role))
}

export const isOwnerUser = (roles: string[] = []) => {
  return roles.some((role) => (OWNER_ROLES as readonly string[]).includes(role))
}

export const getDefaultHomePath = (roles: string[] = []) => {
  return isPropertyUser(roles) ? '/admin/home' : '/owner/home'
}
