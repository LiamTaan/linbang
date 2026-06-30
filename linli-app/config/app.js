const DEFAULT_API_HOST = 'http://127.0.0.1:48080'
const API_PREFIX = '/app-api'
const DEFAULT_AMAP_JS_KEY = '6fce091b3bc68b52bc2c8a5050d32f2e'

const STORAGE_KEYS = {
  apiHost: 'linbang_api_host',
  amapJsKey: 'linbang_amap_js_key',
  amapSecurityJsCode: 'linbang_amap_security_js_code',
  token: 'linbang_token',
  profile: 'linbang_profile',
  roleContext: 'linbang_role_context',
  settings: 'linbang_platform_settings',
  pendingSocialAuth: 'linbang_pending_social_auth'
}

const PUBLIC_PAGES = ['pages/splash/splash', 'pages/login/login', 'pages/login/social_callback']

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

function getRuntimeConfigValue(name) {
  if (typeof window !== 'undefined' && window[name]) {
    return window[name]
  }
  return ''
}

export function getAmapJsKey(settings = {}) {
  return settings.amapJsKey
    || settings.amapWebJsKey
    || settings.mapJsKey
    || uni.getStorageSync(STORAGE_KEYS.amapJsKey)
    || getRuntimeConfigValue('__LINBANG_AMAP_JS_KEY__')
    || DEFAULT_AMAP_JS_KEY
}

export function getAmapSecurityJsCode(settings = {}) {
  return settings.amapSecurityJsCode
    || settings.amapSecurityCode
    || uni.getStorageSync(STORAGE_KEYS.amapSecurityJsCode)
    || getRuntimeConfigValue('__LINBANG_AMAP_SECURITY_JS_CODE__')
    || ''
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
