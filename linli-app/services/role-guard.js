import { switchRole } from '@/api/member'

const ROLE_META = {
  USER: {
    name: '普通用户',
    missingTitle: '请先切换角色',
    missingContent: '当前操作属于普通用户侧，请先切换到普通用户角色。',
    entryUrl: ''
  },
  MERCHANT: {
    name: '服务商',
    missingTitle: '暂不可操作',
    missingContent: '当前操作属于服务商侧，若尚未开通服务商身份，可先前往入驻。',
    entryUrl: '/pages/merchant_entry/merchant_entry'
  },
  PARTNER: {
    name: '区域合作商',
    missingTitle: '暂不可操作',
    missingContent: '当前操作属于区域合作商侧，若尚未开通区域合作商身份，可先前往身份申请。',
    entryUrl: '/pages/identity_application/identity_application'
  },
  PROMOTER: {
    name: '推广员',
    missingTitle: '暂不可操作',
    missingContent: '当前操作属于推广员侧，若尚未开通推广员身份，可先前往身份申请。',
    entryUrl: '/pages/identity_application/identity_application'
  }
}

function getRoleMeta(roleCode) {
  return ROLE_META[roleCode] || {
    name: roleCode || '目标角色',
    missingTitle: '暂不可操作',
    missingContent: '当前角色不支持此操作。',
    entryUrl: ''
  }
}

function showModal(options) {
  return new Promise((resolve) => {
    uni.showModal({
      ...options,
      success: resolve,
      fail: () => resolve({ confirm: false, cancel: true })
    })
  })
}

export async function ensureRoleAccess({ roleContext, requiredRoleCode, actionLabel = '当前操作', refreshContext }) {
  const currentRoleCode = (roleContext && roleContext.currentRoleCode) || ''
  if (currentRoleCode === requiredRoleCode) {
    return true
  }

  const enabledRoleCodes = (roleContext && roleContext.enabledRoleCodes) || []
  const roleMeta = getRoleMeta(requiredRoleCode)

  if (enabledRoleCodes.includes(requiredRoleCode)) {
    const result = await showModal({
      title: '切换角色后继续',
      content: `${actionLabel}仅支持${roleMeta.name}角色，是否立即切换？`
    })
    if (!result.confirm) {
      return false
    }
    await switchRole({
      targetRoleCode: requiredRoleCode
    })
    uni.showToast({
      title: '角色已切换',
      icon: 'success'
    })
    if (typeof refreshContext === 'function') {
      await refreshContext(requiredRoleCode)
    }
    return true
  }

  const result = await showModal({
    title: roleMeta.missingTitle,
    content: roleMeta.missingContent,
    confirmText: roleMeta.entryUrl ? '去处理' : '知道了',
    showCancel: !!roleMeta.entryUrl
  })
  if (result.confirm && roleMeta.entryUrl) {
    uni.navigateTo({
      url: roleMeta.entryUrl
    })
  }
  return false
}
