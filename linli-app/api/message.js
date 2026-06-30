import { get, post, put } from '@/utils/request'

export function getMessageRecordPage(params) {
  return get('/message/record/page', params)
}

export function getMessageRecord(id) {
  return get('/message/record/get', { id })
}

export function getUnreadCount(options = {}) {
  return get('/message/record/unread-count', {}, options)
}

export function markMessageRead(id) {
  return put('/message/record/mark-read', null, { params: { id } })
}

export function markAllMessageRead(messageCategory) {
  return put('/message/record/mark-all-read', null, { params: { messageCategory } })
}

export function getMessageSetting() {
  return get('/message/record/setting/get')
}

export function updateMessageSetting(data) {
  return put('/message/record/setting/update', data)
}

export function submitMessageClickFeedback(data) {
  return post('/message/record/feedback/click', data)
}

export function submitMessageExposeFeedback(data) {
  return post('/message/record/feedback/expose', data)
}
