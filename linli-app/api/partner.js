import { get, post, put } from '@/utils/request'

export function getPartnerWorkbench() {
  return get('/partner/workbench/get')
}

export function getPartnerRegion() {
  return get('/partner/region/get')
}

export function getPartnerDisputePage(params) {
  return get('/partner/dispute/page', params)
}

export function getPartnerPromoteStat() {
  return get('/partner/promote-stat/get')
}

export function getPartnerInstructionPage(params) {
  return get('/partner/instruction/page', params)
}

export function getPartnerPriceReportPage(params) {
  return get('/partner/price-report/page', params)
}

export function createPartnerCoordination(data) {
  return post('/partner/dispute/coordination/create', data)
}

export function createPartnerPriceReport(data) {
  return post('/partner/price-report/create', data)
}

export function withdrawPartnerPriceReport(id) {
  return put('/partner/price-report/withdraw', { id })
}
