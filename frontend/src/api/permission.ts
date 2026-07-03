import request from '@/utils/request'
import type {
  ApiResult,
  PermissionItem,
  PermissionSave,
  PermissionUpdate,
} from '@/utils/api-types'

export const getPermissionTree = (): Promise<ApiResult<PermissionItem[]>> => {
  return request.get<PermissionItem[]>('/api/v1/permissions/tree')
}

export const savePermission = (data: PermissionSave): Promise<ApiResult<void>> => {
  return request.post<void>('/api/v1/permissions', data)
}

export const updatePermission = (data: PermissionUpdate): Promise<ApiResult<void>> => {
  return request.put<void>('/api/v1/permissions', data)
}

export const deletePermission = (id: number): Promise<ApiResult<void>> => {
  return request.delete<void>(`/api/v1/permissions/${id}`)
}
