import { get, post } from '@/utils/request'

export function createRefund(data) {
  return post('/pay/refund/create', data)
}

export function getRefundPage(params) {
  return get('/pay/refund/page', params)
}

export function getRefund(id) {
  return get('/pay/refund/get', { id })
}
