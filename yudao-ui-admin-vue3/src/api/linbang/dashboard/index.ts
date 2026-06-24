import request from '@/config/axios'

export interface DashboardOverview {
  todayOrderCount: number
  todayTradeAmount: number
  todayNewUserCount: number
  completionRate: number
  pendingAuditCount: number
  abnormalOrderCount: number
  riskAlertCount: number
  refundPendingCount: number
  pendingRoleApplyCount: number
  expiringQualificationCount: number
  pendingPriceReportCount: number
  pendingPushTaskCount: number
  failedPushTaskCount: number
}

export interface DashboardTrendPoint {
  statDate: string
  orderCount: number
  tradeAmount: number
  newUserCount: number
  withdrawAmount: number
}

export const DashboardApi = {
  getOverview: async () => {
    return await request.get<DashboardOverview>({ url: '/dashboard/overview' })
  },
  getOrderStat: async () => {
    return await request.get<DashboardTrendPoint[]>({ url: '/dashboard/order-stat' })
  },
  getFinanceStat: async () => {
    return await request.get<DashboardTrendPoint[]>({ url: '/dashboard/finance-stat' })
  },
  getUserStat: async () => {
    return await request.get<DashboardTrendPoint[]>({ url: '/dashboard/user-stat' })
  }
}
