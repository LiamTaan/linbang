import request from '@/config/axios'

export const LINBANG_DYNAMIC_KEY_HEADER = 'X-Linbang-Dynamic-Key-Token'

export interface AdminDynamicKeyVerifyReqVO {
  password: string
}

export interface AdminDynamicKeyVerifyRespVO {
  verifyToken: string
  expireTime?: string
}

export const buildDynamicKeyHeaders = (verifyToken?: string) => {
  if (!verifyToken) {
    return undefined
  }
  return {
    [LINBANG_DYNAMIC_KEY_HEADER]: verifyToken
  }
}

export const AdminDynamicKeyApi = {
  verify: async (data: AdminDynamicKeyVerifyReqVO) => {
    return await request.post<AdminDynamicKeyVerifyRespVO>({
      url: '/security/dynamic-key/verify',
      data
    })
  }
}
