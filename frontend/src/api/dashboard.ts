import request from '@/utils/request'
import type { ApiResult } from '@/utils/api-types'

export interface DashboardStats {
  totalUsers: number
  totalRoles: number
  totalCommunities: number
  totalBuildings: number
  totalHouses: number
  occupiedHouses: number
  totalOwners: number
  totalRepairs: number
  pendingRepairs: number
  processingRepairs: number
  completedRepairs: number
  totalComplaints: number
  pendingComplaints: number
  totalBills: number
  unpaidBills: number
  paidBills: number
  overdueBills: number
  totalPaymentAmount: number
  totalOverdueAmount: number
  billStatusDistribution: Record<string, number>
  repairTypeDistribution: Record<string, number> | null
  complaintTypeDistribution: Record<string, number>
}

export const getDashboardStats = (): Promise<ApiResult<DashboardStats>> => {
  return request.get<DashboardStats>('/api/v1/dashboard/stats')
}
