// src/api/complaint.ts
import request from '@/utils/request'
import type { ApiResult } from '@/utils/api-types'

// ============================================================
// 类型定义
// ============================================================

export interface ComplaintVO {
  id: number
  complaintNo: string
  ownerName?: string
  type: number
  typeName: string
  title: string
  content: string
  contactPhone?: string
  status: number
  statusName: string
  replyContent?: string
  replyTime?: string
  createTime: string
}

export interface ComplaintQuery {
  status?: number
  type?: number
  keyword?: string
  pageNum?: number
  pageSize?: number
  [key: string]: string | number | boolean | null | undefined
}

export interface ComplaintPageResult {
  total: number
  pages: number
  current: number
  size: number
  records: ComplaintVO[]
}

export interface ApplyComplaintDTO {
  type: number
  title: string
  content: string
  contactPhone?: string
}

export interface ReplyComplaintDTO {
  complaintId: number
  replyContent: string
}

// ============================================================
// 接口定义
// ============================================================

/**
 * 接口：GET /api/v1/complaints/page
 * 功能：分页查询投诉列表（支持状态筛选）
 */
export const getComplaintPage = (params?: ComplaintQuery) => {
  return request.get<ComplaintPageResult>('/api/v1/complaints/page', { params })
}

/**
 * 接口：POST /api/v1/owner/complaints
 * 功能：提交投诉建议
 */
export const applyComplaint = (data: ApplyComplaintDTO) => {
  return request.post<{ id: number; complaintNo: string }>('/api/v1/owner/complaints', data)
}

/**
 * 接口：GET /api/v1/complaints/{complaintId}
 * 功能：查询投诉详情
 */
export const getComplaintDetail = (complaintId: number) => {
  return request.get<ComplaintVO>(`/api/v1/complaints/${complaintId}`)
}

/**
 * 接口：PUT /api/v1/complaints/reply
 * 功能：物业端回复投诉
 */
export const replyComplaint = (data: ReplyComplaintDTO) => {
  return request.put<void>('/api/v1/complaints/reply', data)
}

/**
 * 接口：PUT /api/v1/complaint/{complaintId}/close
 * 功能：关闭投诉
 */
export const closeComplaint = (complaintId: number) => {
  return request.put<void>(`/api/v1/complaint/${complaintId}/close`)
}

/**
 * 接口：PUT /api/v1/owner/complaints/{id}/cancel
 * 功能：取消投诉（业主端）
 */
export const cancelComplaint = (id: number) => {
  return request.put<void>(`/api/v1/owner/complaints/${id}/cancel`)
}
