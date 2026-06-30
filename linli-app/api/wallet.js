import { del, get, post, put } from '@/utils/request'

export function getWalletAccount(options = {}) {
  return get('/wallet/account/get', {}, options)
}

export function getBankCardPage(params) {
  return get('/wallet/bank-card/page', params)
}

export function getBankCard(id) {
  return get('/wallet/bank-card/get', { id })
}

export function createBankCard(data) {
  return post('/wallet/bank-card/create', data)
}

export function updateBankCard(data) {
  return put('/wallet/bank-card/update', data)
}

export function setDefaultBankCard(data) {
  return put('/wallet/bank-card/default', data)
}

export function deleteBankCard(id) {
  return del('/wallet/bank-card/delete', { id })
}

export function createWithdraw(data) {
  return post('/wallet/withdraw/create', data)
}

export function getWithdrawPage(params) {
  return get('/wallet/withdraw/page', params)
}

export function getWithdraw(id) {
  return get('/wallet/withdraw/get', { id })
}

export function getWalletFlowPage(params) {
  return get('/wallet/flow/page', params)
}

export function getWalletFlow(id) {
  return get('/wallet/flow/get', { id })
}

export function getWalletBillPage(params) {
  return get('/wallet/bill/page', params)
}
