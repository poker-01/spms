// src/api/bill-admin.ts
import request from '@/utils/request'
import type { ApiResult } from '@/utils/api-types'

// ============================================================
// 费用项目管理
// ============================================================

export interface FeeItem {
  id: number
  itemName: string
  itemCode: string
  billType: number
  billTypeName: string
  unitPrice: number
  status: number
  remark?: string
  createTime: string
}

export interface FeeItemQuery {
  keyword?: string
  pageNum?: number
  pageSize?: number
  [key: string]: string | number | boolean | null | undefined
}

/**
 * 接口：GET /api/v1/admin/fee-items
 * 功能：获取费用项目列表（分页）
 */
export const getFeeItems = (params?: FeeItemQuery) => {
  return request.get<{ records: FeeItem[]; total: number }>('/api/v1/admin/fee-items', { params })
}

/**
 * 接口：GET /api/v1/admin/fee-items/all
 * 功能：获取所有启用的费用项目（用于下拉选择）
 */
export const getAllFeeItems = () => {
  return request.get<FeeItem[]>('/api/v1/admin/fee-items/all')
}

/**
 * 接口：POST /api/v1/admin/fee-items
 * 功能：新增费用项目
 */
export const createFeeItem = (data: Omit<FeeItem, 'id' | 'createTime' | 'billTypeName'>) => {
  return request.post<void>('/api/v1/admin/fee-items', data)
}

/**
 * 接口：PUT /api/v1/admin/fee-items
 * 功能：更新费用项目
 */
export const updateFeeItem = (data: FeeItem) => {
  return request.put<void>('/api/v1/admin/fee-items', data)
}

/**
 * 接口：PUT /api/v1/admin/fee-items/{id}/status
 * 功能：切换费用项目状态
 */
export const toggleFeeItemStatus = (id: number) => {
  return request.put<void>(`/api/v1/admin/fee-items/${id}/status`)
}

// ============================================================
// 账单管理
// ============================================================

export interface BillItem {
  id: number
  billNo: string
  ownerName?: string
  houseInfo?: string
  itemName: string
  itemType: number
  amount: number
  status: number
  statusName: string
  deadline: string
  payTime?: string
  createTime: string
}

export interface BillQuery {
  status?: number
  billType?: number
  keyword?: string
  startDate?: string
  endDate?: string
  pageNum?: number
  pageSize?: number
  [key: string]: string | number | boolean | null | undefined
}

/**
 * 接口：GET /api/v1/admin/bills
 * 功能：获取账单列表（分页）
 */
export const getAdminBills = (params?: BillQuery) => {
  return request.get<{ records: BillItem[]; total: number }>('/api/v1/admin/bills', { params })
}

/**
 * 接口：GET /api/v1/admin/bills/{id}
 * 功能：获取账单详情
 */
export const getAdminBillDetail = (id: number) => {
  return request.get<BillItem>(`/api/v1/admin/bills/${id}`)
}

/**
 * 接口：POST /api/v1/admin/bills/generate
 * 功能：批量生成账单
 * 请求体：{ communityId, feeItemId, period, deadline }
 */
export const generateBills = (data: {
  communityId: number
  feeItemId: number
  period: string
  deadline: string
}) => {
  return request.post<void>('/api/v1/admin/bills/generate', data)
}

/**
 * 接口：DELETE /api/v1/admin/bills/{id}
 * 功能：删除账单（仅待缴费状态可删除）
 */
export const deleteBill = (id: number) => {
  return request.delete<void>(`/api/v1/admin/bills/${id}`)
}
