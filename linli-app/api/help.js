import { get, post } from '@/utils/request'

export function createHelpFeedback(data) {
  return post('/help/feedback/create', data)
}

export function getHelpFeedbackPage(params) {
  return get('/help/feedback/page', params)
}

export function getHelpFeedback(id) {
  return get('/help/feedback/get', { id })
}
