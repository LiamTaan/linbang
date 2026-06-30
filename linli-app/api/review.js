import { get, post, put } from '@/utils/request'

export function createComplaint(data) {
  return post('/review/complaint/create', data)
}

export function getComplaintPage(params) {
  return get('/review/complaint/page', params)
}

export function getComplaint(id) {
  return get('/review/complaint/get', { id })
}

export function createAppeal(data) {
  return post('/review/appeal/create', data)
}

export function getAppealPage(params) {
  return get('/review/appeal/page', params)
}

export function getAppeal(id) {
  return get('/review/appeal/get', { id })
}

export function createReview(data) {
  return post('/review/comment/create', data)
}

export function updateReview(data) {
  return put('/review/comment/update', data)
}

export function getReviewPage(params) {
  return get('/review/comment/page', params)
}

export function getReview(id) {
  return get('/review/comment/get', { id })
}

export function getPendingReviewUnits(options = {}) {
  return get('/review/comment/pending-units', {}, options)
}

export function getMerchantReviewSummary(merchantId) {
  return get('/review/comment/merchant-summary', { merchantId })
}

export function getCredit() {
  return get('/review/credit/get')
}

export function getCreditBenefits() {
  return get('/review/credit/benefits')
}

export function getCreditRecordPage(params) {
  return get('/review/credit-record/page', params)
}

export function getCreditRecord(id) {
  return get('/review/credit-record/get', { id })
}
