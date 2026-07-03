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

export const getOwnerHome = (): Promise<ApiResult<OwnerHomeData>> => {
  return request.get<OwnerHomeData>('/api/v1/owner/home')
}

export const getOwnerBills = (): Promise<ApiResult<OwnerBill[]>> => {
  return request.get<OwnerBill[]>('/api/v1/owner/bills')
}

export const getOwnerRepairs = (): Promise<ApiResult<OwnerRepair[]>> => {
  return request.get<OwnerRepair[]>('/api/v1/owner/repairs')
}

export const applyRepair = (data: RepairApplyData): Promise<ApiResult<void>> => {
  return request.post<void>('/api/v1/owner/repairs', data)
}

export const getOwnerComplaints = (): Promise<ApiResult<OwnerComplaint[]>> => {
  return request.get<OwnerComplaint[]>('/api/v1/owner/complaints')
}

export const applyComplaint = (data: ComplaintApplyData): Promise<ApiResult<void>> => {
  return request.post<void>('/api/v1/owner/complaints', data)
}
