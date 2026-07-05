// src/api/complaint.ts
import request from '@/utils/request'
import type { ApiResult } from '@/utils/api-types'

export interface ComplaintItem {
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
  records: ComplaintItem[]
}

// ===== 业主端 =====
export const getOwnerComplaints = (params?: ComplaintQuery) => {
  return request.get<ComplaintPageResult>('/api/v1/owner/complaints', { params })
}

export const applyComplaint = (data: {
  type: number
  title: string
  content: string
  contactPhone?: string
}) => {
  return request.post<{ id: number }>('/api/v1/owner/complaints', data)
}

export const cancelComplaint = (id: number) => {
  return request.put<void>(`/api/v1/owner/complaints/${id}/cancel`)
}

export const getComplaintDetail = (id: number) => {
  return request.get<ComplaintItem>(`/api/v1/owner/complaints/${id}`)
}

// ===== 管理端 =====
export const getAdminComplaints = (params?: ComplaintQuery) => {
  return request.get<ComplaintPageResult>('/api/v1/admin/complaints', { params })
}

export const replyComplaint = (id: number, data: { replyContent: string }) => {
  return request.put<void>(`/api/v1/admin/complaints/${id}/reply`, data)
}
