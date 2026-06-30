import { get, post, put } from '@/utils/request'

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
