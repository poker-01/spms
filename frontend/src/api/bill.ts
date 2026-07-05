// src/api/bill.ts
import request from '@/utils/request'
import type { ApiResult } from '@/utils/api-types'

export interface BillItem {
  id: number
  billNo: string
  itemName: string
  itemType: number
  amount: number
  status: number
  statusName: string
  houseInfo?: string
  period?: string
  deadline: string
  payTime?: string
  createTime: string
}

export interface BillQuery {
  status?: number
  billType?: number
  keyword?: string
  pageNum?: number
  pageSize?: number
  [key: string]: string | number | boolean | null | undefined
}

export interface BillPageResult {
  total: number
  pages: number
  current: number
  size: number
  records: BillItem[]
}

// ===== 业主端 =====

/**
 * 接口：GET /api/v1/owner/bills
 * 功能：获取业主账单列表（分页）
 */
export const getOwnerBills = (params?: BillQuery) => {
  return request.get<BillPageResult>('/api/v1/owner/bills', { params })
}

/**
 * 接口：GET /api/v1/owner/bills/{id}
 * 功能：获取账单详情
 */
export const getBillDetail = (id: number) => {
  return request.get<BillItem>(`/api/v1/owner/bills/${id}`)
}

/**
 * 接口：POST /api/v1/owner/bills/{id}/pay
 * 功能：缴费
 * 请求体：{ payMethod: 'wechat' | 'alipay' | 'cash' | 'bank' }
 */
export const payBill = (id: number, data: { payMethod: string }) => {
  return request.post<void>(`/api/v1/owner/bills/${id}/pay`, data)
}
