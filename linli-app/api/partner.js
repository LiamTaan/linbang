import { get, post, put } from '@/utils/request'

export function getPartnerWorkbench() {
  return get('/partner/workbench/get')
}

export function getPartnerRegion() {
  return get('/partner/region/get')
}

export function getPartnerEntryAuditPage(params) {
  return get('/partner/entry-audit/page', params)
}

export function getPartnerEntryAudit(id) {
  return get('/partner/entry-audit/get', { id })
}

export function auditPartnerEntry(data) {
  return post('/partner/entry-audit/audit', data)
}

export function getPartnerDisputePage(params) {
  return get('/partner/dispute/page', params)
}

export function getPartnerDispute(disputeType, disputeId) {
  return get(`/partner/dispute/${disputeType}/${disputeId}`)
}

export function createPartnerCoordination(data) {
  return post('/partner/dispute/coordination/create', data)
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

export function createPartnerPriceReport(data) {
  return post('/partner/price-report/create', data)
}

export function withdrawPartnerPriceReport(id) {
  return put('/partner/price-report/withdraw', { id })
}
