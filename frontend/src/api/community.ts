// src/api/community.ts
import request from '@/utils/request'

export interface Community {
  id: number
  name: string
}

/**
 * 接口：GET /api/v1/communities/list
 * 功能：查询全部小区（下拉列表用）
 */
export const getCommunityList = () => {
  return request.get<Community[]>('/api/v1/communities/list')
}
