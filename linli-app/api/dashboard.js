import { get } from '@/utils/request'

export function getDashboardOverview() {
  return get('/dashboard/overview')
}

export function getDashboardOrderStat() {
  return get('/dashboard/order-stat')
}

export function getDashboardFinanceStat() {
  return get('/dashboard/finance-stat')
}

export function getDashboardUserStat() {
  return get('/dashboard/user-stat')
}
