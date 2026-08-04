import { getSocialAuthorizeUrl, socialLogin } from '@/api/auth'
import { applyLoginSession, redirectAfterLogin } from '@/services/session'
import { clearPendingSocialAuth, getPendingSocialAuth, setPendingSocialAuth } from '@/utils/auth'
import { normalizeSocialAuthorizeUrl, openExternalHttpsUrl } from '@/utils/security'

export const SOCIAL_TYPES = {
  WECHAT: 32,
  ALIPAY: 40
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

function extractAuthorizeState(url) {
  try {
    const state = new URL(url).searchParams.get('state') || ''
    return state.length <= 512 ? state : ''
  } catch (error) {
    return ''
  }
}

async function prepareAuthorize(type) {
  const redirectUri = buildRedirectUri(type)
  const rawUrl = await getSocialAuthorizeUrl({
    type,
    redirectUri
  })
  const url = normalizeSocialAuthorizeUrl(rawUrl, type)
  const state = extractAuthorizeState(url)
  if (!url || !state) {
    throw new Error('授权地址校验失败')
  }
  setPendingSocialAuth({
    type,
    state,
    redirectUri
  })
  return { url, state, redirectUri }
}

function openAuthorizeUrl(authorize) {
  if (!openExternalHttpsUrl(authorize.url)) {
    throw new Error('当前环境无法打开授权页面')
  }
  return {
    manualCallback: true,
    state: authorize.state,
    redirectUri: authorize.redirectUri
  }
}

export async function startSocialAuthorize(type) {
  const provider = getProviderByType(type)
  const authorize = await prepareAuthorize(type)
  if (provider) {
    try {
      const nativeResult = await requestNativeCode(provider, authorize.state)
      return {
        type,
        ...nativeResult
      }
    } catch (error) {
      if (type === SOCIAL_TYPES.WECHAT) {
        return openAuthorizeUrl(authorize)
      }
      clearPendingSocialAuth()
      throw error
    }
  }
  return openAuthorizeUrl(authorize)
}

export async function finishSocialLogin(payload, redirect) {
  const pending = getPendingSocialAuth()
  if (!pending || Number(pending.type) !== Number(payload.type) || pending.state !== payload.state) {
    clearPendingSocialAuth()
    throw new Error('授权状态已失效，请重新发起登录')
  }
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
