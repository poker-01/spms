import request from '@/utils/request'
import type { ApiResult } from '@/utils/api-types'

// ============================================================
// 类型定义
// ============================================================

/**
 * 报修记录VO
 */
export interface RepairVO {
  id: number
  orderNo: string
  repairType: number
  repairTypeName: string
  repairDesc: string
  repairPhone: string
  status: number
  statusName: string
  repairCost?: number
  evaluateScore?: number
  evaluateComment?: string
  createTime: string
  repairTime?: string
}

/**
 * 报修查询参数
 * 添加索引签名以兼容 request 的 params 类型
 */
export interface RepairQuery {
  status?: number
  keyword?: string
  pageNum?: number
  pageSize?: number
  [key: string]: string | number | boolean | null | undefined
}

/**
 * 报修分页结果
 */
export interface RepairPageResult {
  total: number
  pages: number
  current: number
  size: number
  records: RepairVO[]
}

/**
 * 提交报修请求体
 */
export interface ApplyRepairDTO {
  repairType: number
  repairDesc: string
  repairPhone: string
}

/**
 * 评价报修请求体
 */
export interface EvaluateRepairDTO {
  score: number
  comment?: string
}

/**
 * 派单请求体
 */
export interface AssignRepairDTO {
  repairerId: number
}

/**
 * 完成报修请求体
 */
export interface CompleteRepairDTO {
  repairCost: number
}

// ============================================================
// 业主端接口
// ============================================================

/**
 * 接口：GET /api/v1/owner/repairs
 * 功能：获取业主报修列表（分页）
 * 参数：{ status?, keyword?, pageNum?, pageSize? }
 * 返回：ApiResult<RepairPageResult>
 */
export const getOwnerRepairs = (params?: RepairQuery) => {
  return request.get<RepairPageResult>('/api/v1/owner/repairs', { params })
}

/**
 * 接口：POST /api/v1/owner/repairs
 * 功能：提交报修
 * 请求体：{ repairType, repairDesc, repairPhone }
 * 返回：ApiResult<{ id: number }>
 */
export const applyRepair = (data: ApplyRepairDTO) => {
  return request.post<{ id: number }>('/api/v1/owner/repairs', data)
}

/**
 * 接口：PUT /api/v1/owner/repairs/{id}/cancel
 * 功能：取消报修（仅待处理状态可取消）
 * 返回：ApiResult<void>
 */
export const cancelRepair = (id: number) => {
  return request.put<void>(`/api/v1/owner/repairs/${id}/cancel`)
}

/**
 * 接口：GET /api/v1/owner/repairs/{id}
 * 功能：获取报修详情
 * 返回：ApiResult<RepairVO>
 */
export const getRepairDetail = (id: number) => {
  return request.get<RepairVO>(`/api/v1/owner/repairs/${id}`)
}

/**
 * 接口：POST /api/v1/owner/repairs/{id}/evaluate
 * 功能：评价报修（仅已完成状态可评价）
 * 请求体：{ score, comment? }
 * 返回：ApiResult<void>
 */
export const evaluateRepair = (id: number, data: EvaluateRepairDTO) => {
  return request.post<void>(`/api/v1/owner/repairs/${id}/evaluate`, data)
}

// ============================================================
// 管理端接口
// ============================================================

/**
 * 管理端报修查询参数
 */
export interface AdminRepairQuery extends RepairQuery {
  repairType?: number
}

/**
 * 接口：GET /api/v1/admin/repairs
 * 功能：获取所有报修列表（管理端，含分页和筛选）
 * 参数：{ status?, repairType?, keyword?, pageNum?, pageSize? }
 * 返回：ApiResult<RepairPageResult>
 */
export const getAdminRepairs = (params?: AdminRepairQuery) => {
  return request.get<RepairPageResult>('/api/v1/admin/repairs', { params })
}

/**
 * 接口：PUT /api/v1/admin/repairs/{id}/assign
 * 功能：派单（分配维修人员）
 * 请求体：{ repairerId: number }
 * 返回：ApiResult<void>
 */
export const assignRepair = (id: number, data: AssignRepairDTO) => {
  return request.put<void>(`/api/v1/admin/repairs/${id}/assign`, data)
}

/**
 * 接口：PUT /api/v1/admin/repairs/{id}/complete
 * 功能：完成报修（设置费用）
 * 请求体：{ repairCost: number }
 * 返回：ApiResult<void>
 */
export const completeRepair = (id: number, data: CompleteRepairDTO) => {
  return request.put<void>(`/api/v1/admin/repairs/${id}/complete`, data)
}
