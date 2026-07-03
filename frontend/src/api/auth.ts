import request from '@/utils/request'
import type { ApiResult, LoginData, LoginParams, MenuItem, UserInfo } from '@/utils/api-types'

export const login = (data: LoginParams): Promise<ApiResult<LoginData>> => {
  return request.post<LoginData>('/api/v1/auth/login', data)
}

export const getUserInfo = (): Promise<ApiResult<UserInfo>> => {
  return request.get<UserInfo>('/api/v1/auth/info')
}

export const getMenus = (): Promise<ApiResult<MenuItem[]>> => {
  return request.get<MenuItem[]>('/api/v1/auth/menus')
}

export const logout = (): Promise<ApiResult<void>> => {
  return request.post<void>('/api/v1/auth/logout')
}
