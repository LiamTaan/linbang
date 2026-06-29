import { get, post } from '@/utils/request'

export function sendSmsCode(data) {
  return post('/member/auth/send-sms-code', data, { auth: false })
}

export function loginBySms(data) {
  return post('/member/auth/login', data, { auth: false })
}

export function accountLogin(data) {
  return post('/member/auth/account-login', data, { auth: false })
}

export function accountRegister(data) {
  return post('/member/auth/account-register', data, { auth: false })
}

export function refreshLoginToken(data) {
  return post('/member/auth/refresh-token', data, { auth: false })
}

export function getRegisterAgreement() {
  return get('/member/auth/register-agreement/get', {}, { auth: false })
}

export function getRegisterReminder(params) {
  return get('/member/auth/register-reminder/get', params, { auth: false })
}

export function ackRegisterReminder(data) {
  return post('/member/auth/register-reminder/ack', data, { auth: false })
}

export function getSocialAuthorizeUrl(params) {
  return get('/member/auth/social-auth-redirect', params, { auth: false })
}

export function socialLogin(data) {
  return post('/member/auth/social-login', data, { auth: false })
}

export function socialBindMobile(data) {
  return post('/member/auth/social-bind-mobile', data, { auth: false })
}

export function logout() {
  return post('/member/auth/logout', {}, { silent: true })
}
