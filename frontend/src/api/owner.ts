import request from '@/utils/request'
import type {
  ApiResult,
  ComplaintApplyData,
  OwnerBill,
  OwnerComplaint,
  OwnerHomeData,
  OwnerRepair,
  RepairApplyData,
} from '@/utils/api-types'
import type { BillDetail } from '@/api/bill-admin'

export const getOwnerHome = (): Promise<ApiResult<OwnerHomeData>> => {
  return request.get<OwnerHomeData>('/api/v1/owner/home')
}

export const getOwnerBills = (): Promise<ApiResult<OwnerBill[]>> => {
  return request.get<OwnerBill[]>('/api/v1/owner/bills')
}

export const getOwnerBillDetail = (id: number): Promise<ApiResult<BillDetail>> => {
  return request.get<BillDetail>(`/api/v1/owner/bills/${id}`)
}

export const payOwnerBill = (id: number, payMethod: number): Promise<ApiResult<void>> => {
  return request.post<void>(`/api/v1/owner/bills/${id}/pay`, { payMethod })
}

export const getOwnerRepairs = (): Promise<ApiResult<OwnerRepair[]>> => {
  return request.get<OwnerRepair[]>('/api/v1/owner/repairs')
}

export const getOwnerRepairDetail = (orderId: number): Promise<ApiResult<OwnerRepair>> => {
  return request.get<OwnerRepair>(`/api/v1/owner/repairs/${orderId}`)
}

export const applyRepair = (data: RepairApplyData): Promise<ApiResult<void>> => {
  return request.post<void>('/api/v1/owner/repairs', {
    content: data.content,
    repairType: data.repairType,
    contactPhone: data.contactPhone,
  })
}

export const getOwnerComplaints = (): Promise<ApiResult<OwnerComplaint[]>> => {
  return request.get<OwnerComplaint[]>('/api/v1/owner/complaints')
}

export const applyComplaint = (data: ComplaintApplyData): Promise<ApiResult<void>> => {
  return request.post<void>('/api/v1/owner/complaints', data)
}

export const getOwnerComplaintDetail = (id: number): Promise<ApiResult<OwnerComplaint>> => {
  return request.get<OwnerComplaint>(`/api/v1/owner/complaints/${id}`)
}

export const cancelOwnerComplaint = (id: number): Promise<ApiResult<void>> => {
  return request.put<void>(`/api/v1/owner/complaints/${id}/cancel`)
}
