import { del, get, post, put } from '@/utils/request'

export function getRoleContext(options = {}) {
  return get('/member/role-context/get', {}, options)
}

export function switchRole(data) {
  return post('/member/role-context/switch', data)
}

export function getProfile(options = {}) {
  return get('/member/user/profile', {}, options)
}

export function updateProfile(data) {
  return put('/member/user/update-profile', data)
}

export function getAddressPage(params, options = {}) {
  return get('/member/address/page', params, options)
}

export function getAddress(id) {
  return get('/member/address/get', { id })
}

export function resolveAddressLocation(data) {
  return post('/member/address/resolve-location', data)
}

export function createAddress(data) {
  return post('/member/address/create', data)
}

export function updateAddress(data) {
  return put('/member/address/update', data)
}

export function deleteAddress(id) {
  return del('/member/address/delete', { id })
}

export function getQualificationPage(params, options = {}) {
  return get('/member/qualification/page', params, options)
}

export function getQualification(id) {
  return get('/member/qualification/get', { id })
}

export function getQualificationSummary() {
  return get('/member/qualification/summary/get')
}

export function getQualificationReminderPage(params) {
  return get('/member/qualification/reminder/page', params)
}

export function createQualification(data) {
  return post('/member/qualification/create', data)
}

export function updateQualification(data) {
  return put('/member/qualification/update', data)
}

export function createCertExemption(data) {
  return post('/member/qualification/cert-exemption/create', data)
}

export function getRealName() {
  return get('/member/real-name/get')
}

export function getRealNameProgress() {
  return get('/member/real-name/progress/get')
}

export function startRealNameVerify(data) {
  return post('/member/real-name/start-verify', data)
}

export function createOrUpdateRealName(data) {
  return post('/member/real-name/create-or-update', data)
}

export function getRoleApplyPage(params) {
  return get('/member/role-apply/page', params)
}

export function getRoleApply(id) {
  return get('/member/role-apply/get', { id })
}

export function createRoleApply(data) {
  return post('/member/role-apply/create', data)
}

export function getLoginLogPage(params) {
  return get('/member/security/login-log/page', params)
}

export function updatePassword(data) {
  return put('/member/security/password/update', data)
}
