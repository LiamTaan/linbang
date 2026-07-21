import { get, post } from '@/utils/request'

export function getPromoteCenter(options = {}) {
  return get('/promote/center/get', {}, options)
}

export function getCommissionPage(params) {
  return get('/promote/commission/page', params)
}

export function getInviteCode() {
  return get('/promote/invite-code/get')
}

export function getTeamStats() {
  return get('/promote/team-stats/get')
}

export function bindInviteCode(data, options = {}) {
  return post('/promote/invite-code/bind', data, options)
}

export function generatePromotePoster() {
  return post('/promote/poster/generate', {})
}
