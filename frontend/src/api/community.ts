import request from '@/utils/request'
import type {
  ApiResult,
  PageResult,
  CommunityItem,
  CommunityQuery,
  CommunitySave,
  CommunityUpdate,
} from '@/utils/api-types'

export const pageCommunities = (
  params: CommunityQuery,
): Promise<ApiResult<PageResult<CommunityItem>>> => {
  return request.get<PageResult<CommunityItem>>('/api/v1/communities/page', { params })
}

export const listCommunities = (): Promise<ApiResult<CommunityItem[]>> => {
  return request.get<CommunityItem[]>('/api/v1/communities/list')
}

export const getCommunity = (id: number): Promise<ApiResult<CommunityItem>> => {
  return request.get<CommunityItem>(`/api/v1/communities/${id}`)
}

export const saveCommunity = (data: CommunitySave): Promise<ApiResult<void>> => {
  return request.post<void>('/api/v1/communities', data)
}

export const updateCommunity = (data: CommunityUpdate): Promise<ApiResult<void>> => {
  return request.put<void>('/api/v1/communities', data)
}

export const deleteCommunity = (id: number): Promise<ApiResult<void>> => {
  return request.delete<void>(`/api/v1/communities/${id}`)
}
