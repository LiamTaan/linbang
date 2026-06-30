import { getApiBaseUrl } from '@/config/app'
import {
  clearSession,
  getAccessToken,
  getRefreshToken,
  hasLogin,
  setTokenInfo
} from '@/utils/auth'
import { navigateToLogin } from '@/utils/navigation'

let refreshPromise = null

function buildQueryString(params) {
  const query = []
  Object.keys(params || {}).forEach((key) => {
    const value = params[key]
    if (value === undefined || value === null || value === '') {
      return
    }
    if (Array.isArray(value)) {
      value.forEach((item) => {
        if (item !== undefined && item !== null && item !== '') {
          query.push(`${encodeURIComponent(key)}=${encodeURIComponent(item)}`)
        }
      })
      return
    }
    query.push(`${encodeURIComponent(key)}=${encodeURIComponent(value)}`)
  })
  return query.length ? `?${query.join('&')}` : ''
}

function showError(message) {
  if (!message) {
    return
  }
  uni.showToast({
    title: message.length > 20 ? '请求失败，请稍后重试' : message,
    icon: 'none'
  })
}

function normalizeUrl(url) {
  if (/^https?:\/\//.test(url)) {
    return url
  }
  const base = getApiBaseUrl()
  return `${base}${url.startsWith('/') ? url : `/${url}`}`
}

function buildHeaders(customHeaders = {}, withAuth = true, includeJsonContentType = true) {
  const headers = { ...customHeaders }
  if (includeJsonContentType && !headers['Content-Type']) {
    headers['Content-Type'] = 'application/json; charset=utf-8'
  }
  if (withAuth && hasLogin()) {
    headers.Authorization = `Bearer ${getAccessToken()}`
  }
  return headers
}

async function refreshAccessToken() {
  if (!getRefreshToken()) {
    throw new Error('登录已过期')
  }
  if (!refreshPromise) {
    refreshPromise = new Promise((resolve, reject) => {
      uni.request({
        url: normalizeUrl('/member/auth/refresh-token'),
        method: 'POST',
        data: {
          refreshToken: getRefreshToken()
        },
        header: buildHeaders({}, false),
        success: (response) => {
          const result = response.data || {}
          if (result.code === 0 && result.data) {
            setTokenInfo(result.data)
            resolve(result.data)
            return
          }
          reject(new Error(result.msg || '刷新登录态失败'))
        },
        fail: (error) => reject(error),
        complete: () => {
          refreshPromise = null
        }
      })
    })
  }
  return refreshPromise
}

function handleUnauthorized() {
  clearSession()
  navigateToLogin()
}

export function request(options) {
  const {
    url,
    method = 'GET',
    data,
    params,
    headers,
    auth = true,
    retry = true,
    silent = false,
    raw = false
  } = options

  return new Promise((resolve, reject) => {
    const requestMethod = `${method}`.toUpperCase()
    const shouldSendBody = requestMethod !== 'GET' && requestMethod !== 'DELETE'
    uni.request({
      url: `${normalizeUrl(url)}${buildQueryString(params)}`,
      method,
      data: shouldSendBody ? (data || {}) : undefined,
      header: buildHeaders(headers, auth),
      success: async (response) => {
        const result = response.data || {}
        if (raw) {
          resolve(result)
          return
        }
        if (result.code === 0) {
          resolve(result.data)
          return
        }
        if (result.code === 401 && auth) {
          if (retry) {
            try {
              await refreshAccessToken()
              const nextResult = await request({
                ...options,
                retry: false
              })
              resolve(nextResult)
              return
            } catch (error) {
              handleUnauthorized()
              reject(error)
              return
            }
          }
          handleUnauthorized()
          reject(new Error(result.msg || '登录已过期'))
          return
        }
        if (!silent) {
          showError(result.msg || '请求失败')
        }
        reject(new Error(result.msg || '请求失败'))
      },
      fail: (error) => {
        if (!silent) {
          showError('网络异常，请稍后重试')
        }
        reject(error)
      }
    })
  })
}

export function get(url, params, options = {}) {
  return request({
    url,
    method: 'GET',
    params,
    ...options
  })
}

export function post(url, data, options = {}) {
  return request({
    url,
    method: 'POST',
    data,
    ...options
  })
}

export function put(url, data, options = {}) {
  return request({
    url,
    method: 'PUT',
    data,
    ...options
  })
}

export function del(url, params, options = {}) {
  return request({
    url,
    method: 'DELETE',
    params,
    ...options
  })
}

export function uploadFile(filePath, options = {}) {
  const {
    url = '/infra/file/upload',
    name = 'file',
    formData = {},
    auth = true,
    retry = true,
    silent = false
  } = options

  return new Promise((resolve, reject) => {
    uni.uploadFile({
      url: normalizeUrl(url),
      filePath,
      name,
      formData,
      header: buildHeaders({}, auth, false),
      success: async (response) => {
        let result = {}
        try {
          result = JSON.parse(response.data)
        } catch (error) {
          if (!silent) {
            showError('上传响应解析失败')
          }
          reject(error)
          return
        }
        if (result.code === 0) {
          resolve(result.data)
          return
        }
        if (result.code === 401 && auth) {
          if (retry) {
            try {
              await refreshAccessToken()
              const nextResult = await uploadFile(filePath, {
                ...options,
                retry: false
              })
              resolve(nextResult)
              return
            } catch (error) {
              handleUnauthorized()
              reject(error)
              return
            }
          }
          handleUnauthorized()
          reject(new Error(result.msg || '登录已过期'))
          return
        }
        if (!silent) {
          showError(result.msg || '上传失败')
        }
        reject(new Error(result.msg || '上传失败'))
      },
      fail: (error) => {
        if (!silent) {
          showError('上传失败，请稍后重试')
        }
        reject(error)
      }
    })
  })
}
