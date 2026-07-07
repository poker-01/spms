// src/api/repair.ts
import request from '@/utils/request'
import type { ApiResult } from '@/utils/api-types'

// ============================================================
// 类型定义
// ============================================================

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
  assignTime?: string
  updateTime?: string
  ownerName?: string
  repairerName?: string
  assigneeName?: string
  repairerId?: number
}

export interface RepairQuery {
  status?: number
  repairType?: string
  keyword?: string
  page?: number
  size?: number
  [key: string]: string | number | boolean | null | undefined
}

export interface RepairPageResult {
  total: number
  pages: number
  current: number
  size: number
  records: RepairVO[]
}

// ============================================================
// 请求体类型 - 与后端保持一致
// ============================================================

/**
 * 报修申请请求体
 * 对应后端 RepairApplyRequest
 */
export interface ApplyRepairDTO {
  content: string        // 报修描述
  repairType: string     // 报修类型（如：水电维修、家具维修等）
  contactPhone: string   // 联系电话
}

/**
 * 报修评价请求体
 * 对应后端 RepairEvaluateRequest
 */
export interface EvaluateRepairDTO {
  orderId: number        // ← 改为 orderId
  score: number
  comment?: string
}

/**
 * 派单请求体
 * 对应后端 RepairAssignRequest
 */
export interface AssignRepairDTO {
  orderId: number        // ← 改为 orderId
  assigneeId: number     // ← 改为 assigneeId
}

/**
 * 完成报修请求体
 * 对应后端 RepairCompleteRequest
 */
export interface CompleteRepairDTO {
  orderId: number        // ← 改为 orderId
  repairCost?: number
  repairResult?: string
}

/**
 * 用户信息（维修人员）
 */
export interface RepairerInfo {
  id: number
  userName: string
  fullName: string
  phoneNumber?: string
  roleNames?: string[]
}

// ============================================================
// 用户/维修人员接口
// ============================================================

/**
 * 接口：GET /api/v1/users/by-role/{roleId}
 * 功能：按角色查询用户列表（不分页，用于下拉选择）
 */
export const getUsersByRole = (roleId: number) => {
  return request.get<RepairerInfo[]>(`/api/v1/users/by-role/${roleId}`)
}

/**
 * 接口：GET /api/v1/repairs/repairers
 * 功能：获取维修人员列表（用于派单下拉选择，仅需 repair:assign 权限）
 */
export const getRepairers = () => {
  return request.get<RepairerInfo[]>('/api/v1/repairs/repairers')
}

/**
 * 接口：GET /api/v1/users/{userId}
 * 功能：获取用户详情
 */
export const getUserDetail = (userId: number) => {
  return request.get<RepairerInfo>(`/api/v1/users/${userId}`)
}

// ============================================================
// 报修接口
// ============================================================

/**
 * 接口：GET /api/v1/repairs/page
 * 功能：分页查询报修列表
 */
export const getRepairPage = (params?: RepairQuery) => {
  return request.get<RepairPageResult>('/api/v1/repairs/page', { params })
}

/**
 * 接口：POST /api/v1/owner/repairs
 * 功能：业主提交报修申请
 * 对应后端：RepairApplyRequest
 * 字段：content, repairType, contactPhone
 */
export const applyRepair = (data: ApplyRepairDTO) => {
  return request.post<void>('/api/v1/owner/repairs', {
    content: data.content,
    repairType: data.repairType,
    contactPhone: data.contactPhone,
  })
}

/**
 * 接口：GET /api/v1/repairs/{orderId}
 * 功能：查询报修详情
 */
export const getRepairDetail = (orderId: number) => {
  return request.get<RepairVO>(`/api/v1/repairs/${orderId}`)
}

/**
 * 接口：PUT /api/v1/repairs/assign
 * 功能：派单给维修人员
 * 对应后端：RepairAssignRequest
 * 字段：orderId, assigneeId
 */
export const assignRepair = (data: AssignRepairDTO) => {
  return request.put<void>('/api/v1/repairs/assign', {
    orderId: data.orderId,
    assigneeId: data.assigneeId,
  })
}

/**
 * 接口：PUT /api/v1/repairs/{orderId}/start
 * 功能：开始处理报修
 */
export const startRepair = (orderId: number) => {
  return request.put<void>(`/api/v1/repairs/${orderId}/start`)
}

/**
 * 接口：PUT /api/v1/repairs/complete
 * 功能：确认维修完成
 * 对应后端：RepairCompleteRequest
 * 字段：orderId, repairCost, repairResult
 */
export const completeRepair = (data: CompleteRepairDTO) => {
  return request.put<void>('/api/v1/repairs/complete', {
    orderId: data.orderId,
    repairCost: data.repairCost,
    repairResult: data.repairResult,
  })
}

/**
 * 接口：PUT /api/v1/repairs/evaluate
 * 功能：业主评价报修
 * 对应后端：RepairEvaluateRequest
 * 字段：orderId, score, comment
 */
export const evaluateRepair = (data: EvaluateRepairDTO) => {
  return request.put<void>('/api/v1/repairs/evaluate', {
    orderId: data.orderId,
    score: data.score,
    comment: data.comment,
  })
}

/**
 * 接口：PUT /api/v1/repairs/{orderId}/cancel
 * 功能：取消报修
 */
export const cancelRepair = (orderId: number) => {
  return request.put<void>(`/api/v1/repairs/${orderId}/cancel`)
}
