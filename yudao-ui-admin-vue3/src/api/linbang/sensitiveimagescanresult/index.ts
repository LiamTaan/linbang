import request from '@/config/axios'

export interface SensitiveImageScanResult {
  id: number
  sceneType?: string
  userId?: number
  userNo?: string
  userNickname?: string
  userMobile?: string
  bizType?: string
  bizId?: number
  fileId?: number
  sourceFileUrl?: string
  maskedFileUrl?: string
  ocrText?: string
  qrContent?: string
  hitWords?: string
  scanStatus?: string
  failureReason?: string
  createTime?: string
}

export const SensitiveImageScanResultApi = {
  getPage: async (params: any) => {
    return await request.get({ url: '/risk/sensitive-image-scan-result/page', params })
  }
}
