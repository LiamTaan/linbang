import { get, put } from '@/utils/request'

export function getServiceCategoryList() {
  return get('/merchant/service-category/list')
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
