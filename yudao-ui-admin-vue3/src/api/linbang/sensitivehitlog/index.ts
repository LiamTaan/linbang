import request from '@/config/axios'

export interface SensitiveHitLog {
  id: number
  sceneType?: string
  userId?: number
  userNo?: string
  userNickname?: string
  userMobile?: string
  bizType?: string
  bizId?: number
  wordId?: number
  hitWord?: string
  blockLevel?: string
  strategy?: string
  contentType?: string
  fileId?: number
  ocrTextSnapshot?: string
  qrContentSnapshot?: string
  manualAuditResult?: string
  contentSnapshot?: string
  createTime?: string
}

export const SensitiveHitLogApi = {
  getPage: async (params: any) => {
    return await request.get({ url: '/risk/sensitive-hit-log/page', params })
  },
  get: async (id: number) => {
    return await request.get<SensitiveHitLog>({ url: `/risk/sensitive-hit-log/get?id=${id}` })
  }
}
