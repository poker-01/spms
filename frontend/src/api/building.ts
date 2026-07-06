import request from '@/utils/request'
import type {
  ApiResult,
  PageResult,
  BuildingItem,
  BuildingQuery,
  BuildingSave,
  BuildingUpdate,
} from '@/utils/api-types'

export const pageBuildings = (
  params: BuildingQuery,
): Promise<ApiResult<PageResult<BuildingItem>>> => {
  return request.get<PageResult<BuildingItem>>('/api/v1/buildings/page', { params })
}

export const listBuildings = (): Promise<ApiResult<BuildingItem[]>> => {
  return request.get<BuildingItem[]>('/api/v1/buildings/list')
}

export const listBuildingsByCommunity = (
  communityId: number,
): Promise<ApiResult<BuildingItem[]>> => {
  return request.get<BuildingItem[]>(`/api/v1/buildings/by-community/${communityId}`)
}

export const getBuilding = (id: number): Promise<ApiResult<BuildingItem>> => {
  return request.get<BuildingItem>(`/api/v1/buildings/${id}`)
}

export const saveBuilding = (data: BuildingSave): Promise<ApiResult<void>> => {
  return request.post<void>('/api/v1/buildings', data)
}

export const updateBuilding = (data: BuildingUpdate): Promise<ApiResult<void>> => {
  return request.put<void>('/api/v1/buildings', data)
}

export const deleteBuilding = (id: number): Promise<ApiResult<void>> => {
  return request.delete<void>(`/api/v1/buildings/${id}`)
}
