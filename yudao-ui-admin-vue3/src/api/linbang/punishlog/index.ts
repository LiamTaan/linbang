import request from '@/config/axios'

export interface PunishLog {
  id?: number
  punishType?: string
  recordId?: number
  userId?: number
  userNo?: string
  userNickname?: string
  userMobile?: string
  status?: string
  reason?: string
  sourceBizType?: string
  sourceBizId?: number
  sourceRecordType?: string
  sourceRecordId?: number
  operatorId?: number
  operateTime?: string
  startTime?: string
  endTime?: string
  releaseOperatorId?: number
  releaseTime?: string
  releaseRemark?: string
  extJson?: string
  createTime?: string
}

export const PunishLogApi = {
  getPage: async (params: any) => {
    return await request.get({ url: '/risk/punish-log/page', params })
  },
  get: async (id: number) => {
    return await request.get<PunishLog>({ url: `/risk/punish-log/get?id=${id}` })
  }
}
