import request from '@/utils/request'
import type {
  ApiResult,
  RoleItem,
  RoleSave,
  RoleUpdate,
  RoleAssignPermission,
} from '@/utils/api-types'

export const listRoles = (): Promise<ApiResult<RoleItem[]>> => {
  return request.get<RoleItem[]>('/api/v1/roles')
}

export const saveRole = (data: RoleSave): Promise<ApiResult<void>> => {
  return request.post<void>('/api/v1/roles', data)
}

export const updateRole = (data: RoleUpdate): Promise<ApiResult<void>> => {
  return request.put<void>('/api/v1/roles', data)
}

export const deleteRole = (id: number): Promise<ApiResult<void>> => {
  return request.delete<void>(`/api/v1/roles/${id}`)
}

export const getRolePermissions = (roleId: number): Promise<ApiResult<number[]>> => {
  return request.get<number[]>(`/api/v1/roles/${roleId}/permissions`)
}

export const assignRolePermissions = (
  data: RoleAssignPermission,
): Promise<ApiResult<void>> => {
  return request.post<void>('/api/v1/roles/assign-permissions', data)
}
