const DEFAULT_API_HOST = 'http://127.0.0.1:48080'
const API_PREFIX = '/app-api'

const STORAGE_KEYS = {
  apiHost: 'linbang_api_host',
  token: 'linbang_token',
  profile: 'linbang_profile',
  roleContext: 'linbang_role_context',
  settings: 'linbang_platform_settings',
  pendingSocialAuth: 'linbang_pending_social_auth'
}

const PUBLIC_PAGES = ['pages/login/login', 'pages/login/social_callback']

export const APP_CONFIG = {
  apiPrefix: API_PREFIX,
  defaultApiHost: DEFAULT_API_HOST,
  loginPage: '/pages/login/login',
  socialCallbackPage: '/pages/login/social_callback',
  fileUploadDirectory: 'linbang/app',
  publicPages: PUBLIC_PAGES,
  storageKeys: STORAGE_KEYS
}

export function getApiHost() {
  const customHost = uni.getStorageSync(STORAGE_KEYS.apiHost)
  return customHost || DEFAULT_API_HOST
}

export function getApiBaseUrl() {
  return `${getApiHost()}${API_PREFIX}`
}

export function setApiHost(host) {
  if (!host) {
    uni.removeStorageSync(STORAGE_KEYS.apiHost)
    return
  }
  uni.setStorageSync(STORAGE_KEYS.apiHost, host.replace(/\/$/, ''))
}

export function isPublicPage(route) {
  return PUBLIC_PAGES.includes(route || '')
}
