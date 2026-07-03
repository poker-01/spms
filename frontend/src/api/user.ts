import request from '@/utils/request'
import type {
  ApiResult,
  PageResult,
  UserItem,
  UserQuery,
  UserSave,
  UserStatus,
  UserUpdate,
  UserAssignRole,
} from '@/utils/api-types'

export const pageUsers = (
  params: UserQuery,
): Promise<ApiResult<PageResult<UserItem>>> => {
  return request.get<PageResult<UserItem>>('/api/v1/users/page', { params })
}

export const saveUser = (data: UserSave): Promise<ApiResult<void>> => {
  return request.post<void>('/api/v1/users', data)
}

export const updateUser = (data: UserUpdate): Promise<ApiResult<void>> => {
  return request.put<void>('/api/v1/users', data)
}

export const toggleUserStatus = (data: UserStatus): Promise<ApiResult<void>> => {
  return request.put<void>('/api/v1/users/status', data)
}

export const resetUserPassword = (userId: number): Promise<ApiResult<void>> => {
  return request.put<void>(`/api/v1/users/${userId}/reset-password`)
}

export const deleteUser = (id: number): Promise<ApiResult<void>> => {
  return request.delete<void>(`/api/v1/users/${id}`)
}

export const getUserRoles = (userId: number): Promise<ApiResult<number[]>> => {
  return request.get<number[]>(`/api/v1/users/${userId}/roles`)
}

export const assignUserRoles = (data: UserAssignRole): Promise<ApiResult<void>> => {
  return request.post<void>('/api/v1/users/assign-roles', data)
}
