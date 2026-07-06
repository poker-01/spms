// src/api/bill-admin.ts
import request from '@/utils/request'
import type { PageResult } from '@/utils/api-types'

// ============================================================
// 费用项目管理
// ============================================================

export interface FeeItem {
  id: number
  itemCode: string
  itemName: string
  itemType: number
  itemTypeName?: string
  unitPrice: number
  unit?: string
  calcMethod?: number
  calcMethodName?: string
  isDefault?: number
  status: number
  statusName?: string
  createTime: string
  updateTime?: string
}

export interface FeeItemQuery {
  itemName?: string
  itemCode?: string
  itemType?: number
  status?: number
  page?: number
  size?: number
}

/**
 * 接口：GET /api/v1/finance/fee-items/page
 * 功能：获取费用项目列表（分页）
 */
export const getFeeItems = (params?: FeeItemQuery) => {
  return request.get<PageResult<FeeItem>>('/api/v1/finance/fee-items/page', { params: params as Record<string, string | number | boolean | undefined> })
}

/**
 * 接口：GET /api/v1/finance/fee-items/enabled
 * 功能：获取所有启用的费用项目（用于下拉选择）
 */
export const getAllFeeItems = () => {
  return request.get<FeeItem[]>('/api/v1/finance/fee-items/enabled')
}

/**
 * 接口：POST /api/v1/finance/fee-items
 * 功能：新增费用项目
 */
export const createFeeItem = (data: {
  itemCode: string
  itemName: string
  itemType: number
  unitPrice: number
  unit?: string
  calcMethod?: number
  isDefault?: number
  status?: number
}) => {
  return request.post<void>('/api/v1/finance/fee-items', data)
}

/**
 * 接口：PUT /api/v1/finance/fee-items
 * 功能：更新费用项目
 */
export const updateFeeItem = (data: {
  id: number
  itemCode?: string
  itemName?: string
  itemType?: number
  unitPrice?: number
  unit?: string
  calcMethod?: number
  isDefault?: number
  status?: number
}) => {
  return request.put<void>('/api/v1/finance/fee-items', data)
}

/**
 * 接口：DELETE /api/v1/finance/fee-items/{id}
 * 功能：删除费用项目
 */
export const deleteFeeItem = (id: number) => {
  return request.delete<void>(`/api/v1/finance/fee-items/${id}`)
}

// ============================================================
// 账单管理
// ============================================================

export interface BillItem {
  id: number
  billNo: string
  ownerName?: string
  ownerPhone?: string
  houseNumber?: string
  buildingName?: string
  itemName: string
  billPeriod?: string
  billAmount: number
  paidAmount?: number
  status: number
  statusName?: string
  payDeadline: string
  payTime?: string
  createTime: string
  updateTime?: string
}

export interface PaymentRecord {
  id: number
  paymentNo: string
  billId: number
  billNo: string
  ownerId?: number
  ownerName?: string
  houseNumber?: string
  payAmount: number
  payMethod?: number
  payMethodName?: string
  payTime?: string
  operatorName?: string
  receiptNo?: string
  createTime?: string
}

export interface BillDetail extends BillItem {
  ownerId?: number
  houseId?: number
  communityName?: string
  feeItemId?: number
  itemCode?: string
  payMethod?: number
  payMethodName?: string
  remark?: string
  paymentRecords?: PaymentRecord[]
}

export interface BillQuery {
  billNo?: string
  ownerId?: number
  houseId?: number
  feeItemId?: number
  status?: number
  billPeriod?: string
  startTime?: string
  endTime?: string
  page?: number
  size?: number
}

/**
 * 接口：GET /api/v1/finance/bills/page
 * 功能：获取账单列表（分页）
 */
export const getAdminBills = (params?: BillQuery) => {
  return request.get<PageResult<BillItem>>('/api/v1/finance/bills/page', { params: params as Record<string, string | number | boolean | undefined> })
}

/**
 * 接口：GET /api/v1/finance/bills/{id}
 * 功能：获取账单详情
 */
export const getAdminBillDetail = (id: number) => {
  return request.get<BillDetail>(`/api/v1/finance/bills/${id}`)
}

/**
 * 接口：POST /api/v1/finance/bills/generate
 * 功能：批量生成账单
 * 请求体：{ feeItemId, ownerIds, billPeriod, billAmount, payDeadline, remark }
 */
export const generateBills = (data: {
  feeItemId: number
  ownerIds: number[]
  billPeriod: string
  billAmount: number
  payDeadline?: string
  remark?: string
}) => {
  return request.post<void>('/api/v1/finance/bills/generate', data)
}

/**
 * 接口：POST /api/v1/finance/bills/pay
 * 功能：缴费登记
 * 请求体：{ billId, payAmount, payMethod, receiptNo }
 */
export const payBill = (data: {
  billId: number
  payAmount: number
  payMethod: number
  receiptNo?: string
}) => {
  return request.post<void>('/api/v1/finance/bills/pay', data)
}

/**
 * 接口：DELETE /api/v1/finance/bills/{id}
 * 功能：删除账单（仅待缴费状态可删除）
 */
export const deleteBill = (id: number) => {
  return request.delete<void>(`/api/v1/finance/bills/${id}`)
}

/**
 * 接口：GET /api/v1/finance/bills/stats/status
 * 功能：统计各状态账单数量
 */
export const getBillStatusStats = () => {
  return request.get<Record<string, number>>('/api/v1/finance/bills/stats/status')
}

/**
 * 接口：DELETE /api/v1/finance/bills/batch
 * 功能：批量删除账单（仅待缴费状态可删除）
 */
export const batchDeleteBills = (ids: number[]) => {
  return request.delete<void>('/api/v1/finance/bills/batch', { params: { ids: ids.join(',') } as Record<string, string | number | boolean | undefined> })
}

// ============================================================
// 收费记录查询
// ============================================================

export interface PaymentQuery {
  billNo?: string
  ownerName?: string
  payMethod?: number
  startTime?: string
  endTime?: string
  page?: number
  size?: number
}

/**
 * 接口：GET /api/v1/finance/payments/page
 * 功能：获取收费记录列表（分页）
 */
export const getPaymentRecords = (params?: PaymentQuery) => {
  return request.get<PageResult<PaymentRecord>>('/api/v1/finance/payments/page', { params: params as Record<string, string | number | boolean | undefined> })
}

// ============================================================
// 逾期账单管理
// ============================================================

export interface OverdueBillQuery {
  communityId?: number
  billNo?: string
  ownerName?: string
  billPeriod?: string
  page?: number
  size?: number
}

/**
 * 接口：GET /api/v1/finance/bills/overdue
 * 功能：获取逾期账单列表（分页）
 */
export const getOverdueBills = (params?: OverdueBillQuery) => {
  return request.get<PageResult<BillItem>>('/api/v1/finance/bills/overdue', { params: params as Record<string, string | number | boolean | undefined> })
}

export interface OverdueStats {
  totalOverdueCount: number
  totalOverdueAmount: number
  overdueByBuilding?: Record<string, { count: number; amount: number }>
}

/**
 * 接口：GET /api/v1/finance/bills/stats/overdue
 * 功能：获取逾期账单统计
 */
export const getOverdueStats = () => {
  return request.get<OverdueStats>('/api/v1/finance/bills/stats/overdue')
}

/**
 * 接口：POST /api/v1/finance/bills/urge
 * 功能：催缴账单（发送催缴通知）
 */
export const urgePayment = (data: { billIds: number[] }) => {
  return request.post<void>('/api/v1/finance/bills/urge', data)
}

// ============================================================
// 业主管理
// ============================================================

export interface OwnerOption {
  id: number
  ownerName: string
  ownerPhone?: string
}

export interface OwnerListItem {
  id: number
  ownerName: string
  ownerPhone?: string
  gender?: number
  genderName?: string
  idCard?: string
  houseList?: { id: number; houseNumber: string; buildingName?: string }[]
  status?: number
  statusName?: string
  createTime?: string
}

export interface OwnerListQuery {
  ownerName?: string
  ownerPhone?: string
  houseId?: number
  page?: number
  size?: number
}

/**
 * 接口：GET /api/v1/owners/list
 * 功能：获取业主列表（用于下拉选择）
 */
export const getOwnerList = () => {
  return request.get<OwnerOption[]>('/api/v1/owners/list')
}

/**
 * 接口：GET /api/v1/owners/page
 * 功能：获取业主列表（分页）
 */
export const getOwnerPageList = (params?: OwnerListQuery) => {
  return request.get<PageResult<OwnerListItem>>('/api/v1/owners/page', { params: params as Record<string, string | number | boolean | undefined> })
}
