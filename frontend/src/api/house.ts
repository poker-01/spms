import request from '@/utils/request'
import type {
  ApiResult,
  PageResult,
  HouseItem,
  HouseQuery,
  HouseSave,
  HouseUpdate,
} from '@/utils/api-types'

export const pageHouses = (
  params: HouseQuery,
): Promise<ApiResult<PageResult<HouseItem>>> => {
  return request.get<PageResult<HouseItem>>('/api/v1/houses/page', { params })
}

export const listHouses = (): Promise<ApiResult<HouseItem[]>> => {
  return request.get<HouseItem[]>('/api/v1/houses/list')
}

export const listHousesByBuilding = (
  buildingId: number,
): Promise<ApiResult<HouseItem[]>> => {
  return request.get<HouseItem[]>(`/api/v1/houses/by-building/${buildingId}`)
}

export const listHousesByOwner = (
  ownerId: number,
): Promise<ApiResult<HouseItem[]>> => {
  return request.get<HouseItem[]>(`/api/v1/houses/by-owner/${ownerId}`)
}

export const getHouse = (id: number): Promise<ApiResult<HouseItem>> => {
  return request.get<HouseItem>(`/api/v1/houses/${id}`)
}

export const saveHouse = (data: HouseSave): Promise<ApiResult<void>> => {
  return request.post<void>('/api/v1/houses', data)
}

export const updateHouse = (data: HouseUpdate): Promise<ApiResult<void>> => {
  return request.put<void>('/api/v1/houses', data)
}

export const deleteHouse = (id: number): Promise<ApiResult<void>> => {
  return request.delete<void>(`/api/v1/houses/${id}`)
}
