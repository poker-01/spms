import request from '@/utils/request'
import type {
  ApiResult,
  PageResult,
  OwnerItem,
  OwnerQuery,
  OwnerSave,
  OwnerUpdate,
  OwnerHouseRel,
} from '@/utils/api-types'

export const pageOwners = (
  params: OwnerQuery,
): Promise<ApiResult<PageResult<OwnerItem>>> => {
  return request.get<PageResult<OwnerItem>>('/api/v1/owners/page', { params })
}

export const listOwners = (): Promise<ApiResult<OwnerItem[]>> => {
  return request.get<OwnerItem[]>('/api/v1/owners/list')
}

export const listUnlinkedOwners = (): Promise<ApiResult<OwnerItem[]>> => {
  return request.get<OwnerItem[]>('/api/v1/owners/unlinked')
}

export const getOwnerByHouse = (houseId: number): Promise<ApiResult<OwnerItem>> => {
  return request.get<OwnerItem>(`/api/v1/owners/by-house/${houseId}`)
}

export const listOwnerHouses = (
  ownerId: number,
): Promise<ApiResult<OwnerHouseRel[]>> => {
  return request.get<OwnerHouseRel[]>(`/api/v1/owners/${ownerId}/houses`)
}

export const getOwner = (id: number): Promise<ApiResult<OwnerItem>> => {
  return request.get<OwnerItem>(`/api/v1/owners/${id}`)
}

export const saveOwner = (data: OwnerSave): Promise<ApiResult<void>> => {
  return request.post<void>('/api/v1/owners', data)
}

export const updateOwner = (data: OwnerUpdate): Promise<ApiResult<void>> => {
  return request.put<void>('/api/v1/owners', data)
}

export const deleteOwner = (id: number): Promise<ApiResult<void>> => {
  return request.delete<void>(`/api/v1/owners/${id}`)
}
