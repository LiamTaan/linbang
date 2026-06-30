import { get, post, put } from '@/utils/request'

export function previewOrder(data) {
  return post('/order/info/preview', data)
}

export function createOrder(data) {
  return post('/order/info/create', data)
}

export function getGuaranteeConfig(options = {}) {
  return get('/order/guarantee-config', {}, options)
}

export function matchSplitRule(params) {
  return get('/order/split-rule/match', params)
}

export function getAcceptOrderPage(params, options = {}) {
  return get('/order/accept/page', params, options)
}

export function getOrderPage(params, options = {}) {
  return get('/order/info/page', params, options)
}

export function getOrderDetail(id) {
  return get('/order/info/get', { id })
}

export function updateOrder(data) {
  return put('/order/info/update', data)
}

export function cancelOrder(data) {
  return post('/order/info/cancel', data)
}

export function getOrderUnitPage(params) {
  return get('/order/unit/page', params)
}

export function getOrderUnit(id) {
  return get('/order/unit/get', { id })
}

export function startOrderUnitService(data) {
  return post('/order/unit/start-service', data)
}

export function confirmOrderUnit(data) {
  return post('/order/unit/confirm', data)
}

export function getOrderUnitVerifyCode(unitId) {
  return get('/order/unit/verify-code', { unitId })
}

export function verifyOrderUnit(data) {
  return post('/order/unit/verify', data)
}

export function getOrderUnitVerifyLogs(unitId) {
  return get('/order/unit/verify-log/list', { unitId })
}

export function acceptOrder(data) {
  return post('/order/accept/create', data)
}

export function uploadDeliveryProof(data) {
  return post('/order/unit/upload-delivery-proof', data)
}

export function getAppealProgress(id) {
  return get('/order/appeal/progress', { id })
}
