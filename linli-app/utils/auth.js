import { APP_CONFIG } from '@/config/app'

const { storageKeys } = APP_CONFIG

function readStorage(key, fallback) {
  const value = uni.getStorageSync(key)
  return value || fallback
}

export function getTokenInfo() {
  return readStorage(storageKeys.token, {})
}

export function getAccessToken() {
  return getTokenInfo().accessToken || ''
}

export function getRefreshToken() {
  return getTokenInfo().refreshToken || ''
}

export function getExpiresTime() {
  return getTokenInfo().expiresTime || ''
}

export function hasLogin() {
  return !!getAccessToken()
}

export function setTokenInfo(tokenInfo) {
  const nextValue = {
    ...getTokenInfo(),
    ...tokenInfo
  }
  uni.setStorageSync(storageKeys.token, nextValue)
  return nextValue
}

export function clearTokenInfo() {
  uni.removeStorageSync(storageKeys.token)
}

export function getProfile() {
  return readStorage(storageKeys.profile, null)
}

export function setProfile(profile) {
  uni.setStorageSync(storageKeys.profile, profile)
  return profile
}

export function clearProfile() {
  uni.removeStorageSync(storageKeys.profile)
}

export function getRoleContext() {
  return readStorage(storageKeys.roleContext, null)
}

export function setRoleContext(roleContext) {
  uni.setStorageSync(storageKeys.roleContext, roleContext)
  return roleContext
}

export function clearRoleContext() {
  uni.removeStorageSync(storageKeys.roleContext)
}

export function getPlatformSettings() {
  return readStorage(storageKeys.settings, null)
}

export function setPlatformSettings(settings) {
  uni.setStorageSync(storageKeys.settings, settings)
  return settings
}

export function clearPlatformSettings() {
  uni.removeStorageSync(storageKeys.settings)
}

export function getPendingSocialAuth() {
  return readStorage(storageKeys.pendingSocialAuth, null)
}

export function setPendingSocialAuth(payload) {
  uni.setStorageSync(storageKeys.pendingSocialAuth, payload)
  return payload
}

export function clearPendingSocialAuth() {
  uni.removeStorageSync(storageKeys.pendingSocialAuth)
}

export function clearSession() {
  clearTokenInfo()
  clearProfile()
  clearRoleContext()
  clearPendingSocialAuth()
}
