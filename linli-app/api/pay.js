import { get, post } from '@/utils/request'

export function createPayOrder(data) {
  return post('/linbang/pay/order/create', data)
}

export function getPayOrder(params, options = {}) {
  return get('/linbang/pay/order/get', params, options)
}

export function submitPayOrder(data) {
  return post('/linbang/pay/order/h5/submit', data)
}

export function submitWechatMiniProgramPay(data) {
  return post('/linbang/pay/order/wechat-mini-program/submit', data)
}

export function createRefund(data) {
  return post('/pay/refund/create', data)
}

export function getRefundPage(params) {
  return get('/pay/refund/page', params)
}

export function getRefund(id) {
  return get('/pay/refund/get', { id })
}
