import { APP_CONFIG } from '@/config/app'
import { getSocialAuthorizeUrl, socialLogin } from '@/api/auth'
import { applyLoginSession, redirectAfterLogin } from '@/services/session'
import { setPendingSocialAuth } from '@/utils/auth'

export const SOCIAL_TYPES = {
  WECHAT: 32,
  ALIPAY: 40
}

function createState() {
  return `${Date.now()}_${Math.random().toString(36).slice(2, 10)}`
}

function getProviderByType(type) {
  if (type === SOCIAL_TYPES.WECHAT) {
    return 'weixin'
  }
  if (type === SOCIAL_TYPES.ALIPAY) {
    return 'alipay'
  }
  return ''
}

function buildRedirectUri(type) {
  return `linbang://oauth-callback?type=${type}`
}

function requestNativeCode(provider, state) {
  return new Promise((resolve, reject) => {
    uni.login({
      provider,
      onlyAuthorize: true,
      success: (res) => {
        const code = res.code || (res.authResult && res.authResult.code)
        if (!code) {
          reject(new Error('未获取到授权码'))
          return
        }
        resolve({
          code,
          state
        })
      },
      fail: reject
    })
  })
}

async function openAuthorizeUrl(type, state) {
  const redirectUri = buildRedirectUri(type)
  const url = await getSocialAuthorizeUrl({
    type,
    redirectUri
  })
  setPendingSocialAuth({
    type,
    state,
    redirectUri
  })
  // #ifdef APP-PLUS
  plus.runtime.openURL(url)
  // #endif
  // #ifndef APP-PLUS
  window.location.href = url
  // #endif
  return {
    manualCallback: true,
    state,
    redirectUri
  }
}

export async function startSocialAuthorize(type) {
  const provider = getProviderByType(type)
  const state = createState()
  if (provider) {
    try {
      const nativeResult = await requestNativeCode(provider, state)
      return {
        type,
        ...nativeResult
      }
    } catch (error) {
      if (type === SOCIAL_TYPES.WECHAT) {
        return openAuthorizeUrl(type, state)
      }
      throw error
    }
  }
  return openAuthorizeUrl(type, state)
}

export async function finishSocialLogin(payload, redirect) {
  const loginResp = await socialLogin(payload)
  if (loginResp && loginResp.bindRequired) {
    setPendingSocialAuth({
      type: payload.type,
      code: payload.code,
      state: payload.state,
      socialOpenid: loginResp.socialOpenid || '',
      socialNickname: loginResp.socialNickname || '',
      socialAvatar: loginResp.socialAvatar || '',
      registerReminder: loginResp.registerReminder || null
    })
    return {
      bindRequired: true,
      loginResp
    }
  }
  await applyLoginSession(loginResp)
  redirectAfterLogin(redirect)
  return {
    bindRequired: false,
    loginResp
  }
}
