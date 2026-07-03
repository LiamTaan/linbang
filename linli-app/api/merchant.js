import { del, get, post, put } from '@/utils/request'

export function getServiceCategoryList(keyword, options = {}) {
  const useOptions = keyword && typeof keyword === 'object' && !Array.isArray(keyword)
    ? keyword
    : options
  const params = typeof keyword === 'string' && keyword.trim()
    ? { keyword: keyword.trim() }
    : {}
  return get('/merchant/service-category/list', params, useOptions)
}

export function updateSelectedCategory(data) {
  return put('/merchant/service-category/selected/update', data)
}

export function getMerchantProfile(options = {}) {
  return get('/merchant/info/profile', {}, options)
}

export function getMerchantAcceptStatus() {
  return get('/merchant/info/accept-status/get')
}

export function updateMerchantAcceptStatus(data) {
  return put('/merchant/info/accept-status/update', data)
}

export function getMerchantDispatchSetting() {
  return get('/merchant/dispatch-setting/get')
}

export function updateMerchantDispatchSetting(data) {
  return put('/merchant/dispatch-setting/update', data)
}

export function getMerchantEntry(options = {}) {
  return get('/merchant/entry/get', {}, options)
}

export function getMerchantOnboardingProgress(options = {}) {
  return get('/merchant/entry/onboarding/progress/get', {}, options)
}

export function createMerchantEntry(data) {
  return post('/merchant/entry/create', data)
}

export function getMerchantServicePointPage(params, options = {}) {
  return get('/merchant/service-point/page', params, options)
}

export function createMerchantServicePoint(data) {
  return post('/merchant/service-point/create', data)
}

export function updateMerchantServicePoint(data) {
  return put('/merchant/service-point/update', data)
}

export function updateMerchantServicePointStatus(data) {
  return put('/merchant/service-point/status/update', data)
}

export function deleteMerchantServicePoint(id) {
  return del('/merchant/service-point/delete', { id })
}

export function getMerchantReferencePriceList(options = {}) {
  return get('/merchant/reference-price/list', {}, options)
}

export function createMerchantReferencePrice(data) {
  return post('/merchant/reference-price/create', data)
}

export function updateMerchantReferencePrice(data) {
  return put('/merchant/reference-price/update', data)
}

export function updateMerchantReferencePriceStatus(data) {
  return put('/merchant/reference-price/status/update', data)
}

export function deleteMerchantReferencePrice(id) {
  return del('/merchant/reference-price/delete', { id })
}
