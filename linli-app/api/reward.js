import { get, post } from '@/utils/request'

export function getRewardOrderPage(params, options = {}) {
  return get('/reward-order/page', params, options)
}

export function getParticipatedRewardOrderPage(params, options = {}) {
  return get('/reward-order/participated/page', params, options)
}

export function getRewardOrder(id, options = {}) {
  return get('/reward-order/get', { id }, options)
}

export function createRewardOrder(data, options = {}) {
  return post('/reward-order/create', data, options)
}

export function participateRewardOrder(data, options = {}) {
  return post('/reward-order/participate', data, options)
}
